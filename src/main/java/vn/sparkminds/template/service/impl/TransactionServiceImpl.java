package vn.sparkminds.template.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fireblocks.sdk.model.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.error.http.BadRequestException;
import vn.sparkminds.template.config.error.http.NotFoundException;
import vn.sparkminds.template.entity.*;
import vn.sparkminds.template.entity.enumeration.TransactionStatus;
import vn.sparkminds.template.entity.enumeration.TransactionType;
import vn.sparkminds.template.repository.*;
import vn.sparkminds.template.service.FireblocksService;
import vn.sparkminds.template.service.TransactionService;
import vn.sparkminds.template.service.dto.request.CreateTransactionWithdrawRequestDto;
import vn.sparkminds.template.service.dto.request.TransactionDataDto;
import vn.sparkminds.template.service.dto.request.TransactionWebhookRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static vn.sparkminds.template.constant.ErrorConstants.ERROR_TRANSACTION_WITHDRAW_EXISTED;
import static vn.sparkminds.template.constant.ErrorConstants.ERROR_WALLET_BALANCE_INSUFFICIENT;
import static vn.sparkminds.template.constant.FireblockConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final NetworkRepository networkRepository;
    private final CoinRepository coinRepository;
    private final FireblocksService fireblocksService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    public boolean checkTransaction(TransactionWebhookRequest request) {

        if (request.getType().equalsIgnoreCase(TRANSACTION_CREATED)) {
            Transaction transaction = transactionRepository.findByFireblockTransactionId(request.getData().getId());
            return transaction == null;
        } else return request.getType().equalsIgnoreCase(TRANSACTION_STATUS_UPDATED);
    }

    @Transactional
    @Override
    public void processTransaction(TransactionWebhookRequest request) {
        log.info("Processing transaction: {}", request);

        if (!checkTransaction(request)) return;

        // Get wallet destination and source
        Wallet walletDestination;
        Optional<Wallet> walletSource;

        if (!request.getData().getSource().getType().equalsIgnoreCase(VAULT_ACCOUNT)) {
            walletDestination = walletRepository.findByAddress(request.getData().getDestinationAddress())
                    .orElseThrow(NotFoundException::new);
            walletSource = walletRepository.findByAddress(request.getData().getSourceAddress());

        } else {
            walletDestination = walletRepository.findByUser_VaultIdAndAndCoin_Symbol(
                    request.getData().getDestination().getId(),
                    request.getData().getAssetId());

            walletSource = walletRepository.findWalletByUser_VaultIdAndCoin_Symbol(
                    request.getData().getSource().getId(),
                    request.getData().getAssetId());
        }

        // Get network
        Network network;
        if (walletSource.isPresent()) {
            network = networkRepository.findNetworkByWalletId(walletSource.get().getId())
                    .orElseThrow(NotFoundException::new);
        } else if (walletDestination != null) {
            network = networkRepository.findNetworkByWalletId(walletDestination.getId())
                    .orElseThrow(NotFoundException::new);
        } else {
            throw new NotFoundException();
        }

        // Get coin
        Coin coin = coinRepository.findCoinBySymbol(request.getData().getAssetId());

        // Get transaction status
        TransactionStatus transactionStatus;
        if (request.getType().equalsIgnoreCase(TRANSACTION_CREATED)) {
            transactionStatus = TransactionStatus.CREATED;

        } else {
            transactionStatus = switch (request.getData().getStatus()) {
                case "CANCELLED", "FAILED" -> TransactionStatus.FAILED;
                case "COMPLETED" -> TransactionStatus.COMPLETE;
                default -> TransactionStatus.COMFIRMING;
            };
        }

        // Get from and to address
        String fromAddress = request.getData().getSourceAddress();
        if (fromAddress == null || fromAddress.isBlank()) {
            fromAddress = walletSource.map(Wallet::getAddress).orElse(null);
        }

        String toAddress = request.getData().getDestinationAddress();
        if (toAddress == null || toAddress.isBlank()) {
            toAddress = walletDestination.getAddress();
        }

        // Create transaction
        Transaction transaction = transactionRepository.findByExternalId(request.getData().getExternalTxId());
        if (transaction == null) {
            transaction = transactionRepository.findByFireblockTransactionId(request.getData().getId());
            if (transaction == null) {
                transaction = new Transaction();
                transaction.setType(TransactionType.DEPOSIT);
            }
        }
        transaction.setCoin(coin);
        transaction.setFireblockTransactionId(request.getData().getId());
        transaction.setAmount(request.getData().getAmount());
        transaction.setStatus(transactionStatus);
        transaction.setFromAddress(fromAddress);
        transaction.setToAddress(toAddress);
        transaction.setHashId(request.getData().getTxHash());
        transaction.setTimestamp(Instant.now());
        transaction.setSystemProcessDate(Instant.now());
        transaction.setNetwork(network);
        transaction.setWallet(walletDestination);

        if (request.getData().getExternalTxId() == null) {
            transaction.setExternalId(UUID.randomUUID().toString());
        } else {
            transaction.setExternalId(request.getData().getExternalTxId());
        }

        transactionRepository.save(transaction);

        // Update balance if transaction is completed or failed
        if ("COMPLETED".equalsIgnoreCase(request.getData().getStatus())
                || "FAILED".equalsIgnoreCase(request.getData().getStatus())) {

            log.info("Transaction complete, update balance");
            transaction.setSystemConfirmDate(Instant.now());
            transactionRepository.save(transaction);

            if (walletDestination != null) {
                BigDecimal balanceDestination = fireblocksService
                        .getAssetBalance(walletDestination
                                        .getUser()
                                        .getVaultId(),
                                request.getData().getAssetId());

                walletDestination.setBalance(balanceDestination);
                walletRepository.save(walletDestination);
            }

            if (walletSource.isPresent()) {
                BigDecimal balanceSource = fireblocksService
                        .getAssetBalance(walletSource.get()
                                        .getUser().getVaultId(),
                                request.getData().getAssetId());

                walletSource.get().setBalance(balanceSource);
                walletRepository.save(walletSource.get());
            }

        }
        log.info("Transaction processed successfully with status {}", transaction.getStatus());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void createTransactionWithdraw(CreateTransactionWithdrawRequestDto request, String email) {
        log.info("Creating transaction for from={}, coin={}, amount={}, to={}, address={}", email, request.getAssetId(), request.getAmount(), request.getDestinationVaultId(), request.getAddress());

        // Get user
        User fromUser = userRepository.findByEmailAndIsEnabledIsTrue(email)
                .orElseThrow(NotFoundException::new);

        // Get coin
        Coin coin = coinRepository.findCoinBySymbolAndIsEnabledIsTrue(request.getAssetId())
                .orElseThrow(NotFoundException::new);

        // Get user destination
        var toUser = userRepository.findUserByVaultId(request.getDestinationVaultId());

        //lock wallet
        Wallet wallet = walletRepository.findByUserAndCoin(fromUser, coin)
                .orElseThrow(NotFoundException::new);

        // Check if user has pending withdraw
        boolean hasPendingWithdraw = transactionRepository.existsByFromAddressAndStatus(wallet.getAddress(), TransactionStatus.QUEUED);
        if (hasPendingWithdraw) {
            throw new BadRequestException(ERROR_TRANSACTION_WITHDRAW_EXISTED);
        }

        // Check balance
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException(ERROR_WALLET_BALANCE_INSUFFICIENT);
        }

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setCoin(coin);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.QUEUED);
        transaction.setType(TransactionType.WITHDRAW);

        // Get destination wallet
        Wallet walletDestination = null;
        if (toUser.isPresent()) {
            walletDestination = walletRepository.findByUserAndCoin(toUser.get(), coin).orElse(null);
        }

        transaction.setWallet(walletDestination);
        transaction.setFromAddress(wallet.getAddress());
        transaction.setToAddress(request.getAddress());
        transaction.setExternalId(UUID.randomUUID().toString());
        transaction.setTimestamp(Instant.now());
        transaction.setSystemProcessDate(Instant.now());
        transaction.setNetwork(wallet.getNetwork());
        transactionRepository.save(transaction);

        log.info("Transaction queued successfully with externalId: {}", transaction.getExternalId());
    }

    @Transactional
    @Override
    public List<Transaction> getPendingTransactions(String type) {
        if (type.equalsIgnoreCase("verify")) {
            List<Transaction> transactions = transactionRepository.findByStatus(TransactionStatus.CREATED);
            transactions.addAll(transactionRepository.findByStatus(TransactionStatus.COMFIRMING));
            return transactions;
        } else {
            return transactionRepository.findByStatus(TransactionStatus.QUEUED);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    public void updateTransaction(Long id) {
        log.info("Process transaction id: {}", id);

        transactionRepository.lockById(id).filter(Transaction::isPending).ifPresentOrElse(transaction -> {
            log.info("Transaction found: {}", transaction);

            if (transaction.getFireblockTransactionId() == null) {
                TransactionResponse response = fireblocksService.getTransactionById(transaction.getFireblockTransactionId(), true);
            }
            TransactionResponse response = fireblocksService.getTransactionById(transaction.getExternalId(), false);

            if (response == null) {
                log.error("Transaction not found in Fireblocks: {}", transaction);
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                return;
            }
            try {
                String json = objectMapper.writeValueAsString(response);
                TransactionDataDto data = objectMapper.readValue(json, TransactionDataDto.class);
                TransactionWebhookRequest request = new TransactionWebhookRequest();
                request.setData(data);
                request.setType(TRANSACTION_STATUS_UPDATED);
                request.setTimestamp(Instant.now().toEpochMilli());
                processTransaction(request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            log.info("Transaction processed successfully: {}", transaction);
        }, () -> log.error("Transaction not found or already processed: {}", id));
    }

}

