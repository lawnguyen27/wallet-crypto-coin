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
import vn.sparkminds.template.service.FireblocksTransactionService;
import vn.sparkminds.template.service.TransactionService;
import vn.sparkminds.template.service.VaultService;
import vn.sparkminds.template.service.dto.request.CreateTransactionWithdrawRequestDto;
import vn.sparkminds.template.service.dto.request.TransactionDataDto;
import vn.sparkminds.template.service.dto.request.TransactionWebhookRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static vn.sparkminds.template.constant.ErrorConstants.*;
import static vn.sparkminds.template.constant.FireblockConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final NetworkRepository networkRepository;
    private final CoinRepository coinRepository;
    private final FireblocksTransactionService fireblocksTransactionService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final LimitTokenRepository limitTokenRepository;
    private final VaultService vaultService;


    public boolean checkTransaction(TransactionWebhookRequest request) {
        if (TRANSACTION_CREATED.equalsIgnoreCase(request.getType())) {
            return transactionRepository.findByFireblockTransactionId(request.getData().getId()) == null;
        }
        return TRANSACTION_STATUS_UPDATED.equalsIgnoreCase(request.getType());
    }

    @Override
    @Transactional
    public void processTransaction(TransactionWebhookRequest request) {
        log.info("Processing transaction: {}", request);

        if (!checkTransaction(request)) return;

        Wallet walletDestination = getWalletDestination(request);
        Optional<Wallet> walletSource = getWalletSource(request);
        Network network = resolveNetwork(walletSource, walletDestination);
        Coin coin = coinRepository.findCoinBySymbol(request.getData().getAssetId());
        TransactionStatus transactionStatus = resolveTransactionStatus(request);

        String fromAddress = resolveFromAddress(request, walletSource);
        String toAddress = resolveToAddress(request, walletDestination);

        Transaction transaction = findOrCreateTransaction(request);
        fillTransaction(transaction, request, coin, fromAddress, toAddress, network, transactionStatus);
        transactionRepository.save(transaction);

        switch (transactionStatus) {
            case FAILED -> handleFailedTransaction(transaction, walletSource, walletDestination, request);
            case COMPLETE -> handleCompleteTransaction(transaction, walletSource, walletDestination, coin);
        }

        log.info("Transaction processed successfully with status {}", transaction.getStatus());
    }

    private Wallet getWalletDestination(TransactionWebhookRequest request) {
        if (!VAULT_ACCOUNT.equalsIgnoreCase(request.getData().getSource().getType())) {
            return walletRepository.findByAddress(request.getData().getDestinationAddress())
                    .orElseThrow(NotFoundException::new);
        }
        return walletRepository.findByUser_VaultIdAndAndCoin_Symbol(
                request.getData().getDestination().getId(),
                request.getData().getAssetId()
        );
    }

    private Optional<Wallet> getWalletSource(TransactionWebhookRequest request) {
        if (!VAULT_ACCOUNT.equalsIgnoreCase(request.getData().getSource().getType())) {
            return walletRepository.findByAddress(request.getData().getSourceAddress());
        }
        return walletRepository.findWalletByUser_VaultIdAndCoin_Symbol(
                request.getData().getSource().getId(),
                request.getData().getAssetId()
        );
    }

    private Network resolveNetwork(Optional<Wallet> walletSource, Wallet walletDestination) {
        if (walletSource.isPresent()) {
            return networkRepository.findNetworkByWalletId(walletSource.get().getId())
                    .orElseThrow(NotFoundException::new);
        } else if (walletDestination != null) {
            return networkRepository.findNetworkByWalletId(walletDestination.getId())
                    .orElseThrow(NotFoundException::new);
        }
        throw new NotFoundException();
    }

    private TransactionStatus resolveTransactionStatus(TransactionWebhookRequest request) {
        if (TRANSACTION_CREATED.equalsIgnoreCase(request.getType())) return TransactionStatus.CREATED;
        return switch (request.getData().getStatus()) {
            case "CANCELLED", "FAILED" -> TransactionStatus.FAILED;
            case "COMPLETED" -> TransactionStatus.COMPLETE;
            default -> TransactionStatus.COMFIRMING;
        };
    }

    private String resolveFromAddress(TransactionWebhookRequest request, Optional<Wallet> walletSource) {
        String fromAddress = request.getData().getSourceAddress();
        return (fromAddress == null || fromAddress.isBlank()) ?
                walletSource.map(Wallet::getAddress).orElse(null) : fromAddress;
    }

    private String resolveToAddress(TransactionWebhookRequest request, Wallet walletDestination) {
        String toAddress = request.getData().getDestinationAddress();
        return (toAddress == null || toAddress.isBlank()) ?
                walletDestination.getAddress() : toAddress;
    }

    private Transaction findOrCreateTransaction(TransactionWebhookRequest request) {
        Transaction transaction = transactionRepository.findByExternalId(request.getData().getExternalTxId());
        if (transaction == null) {
            transaction = transactionRepository.findByFireblockTransactionId(request.getData().getId());
            if (transaction == null) {
                transaction = new Transaction();
                transaction.setType(TransactionType.DEPOSIT);
            }
        }
        return transaction;
    }

    private void fillTransaction(Transaction transaction, TransactionWebhookRequest request, Coin coin,
                                 String fromAddress, String toAddress, Network network, TransactionStatus status) {
        transaction.setCoin(coin);
        transaction.setFireblockTransactionId(request.getData().getId());
        transaction.setAmount(request.getData().getAmount());
        transaction.setStatus(status);
        transaction.setFromAddress(fromAddress);
        transaction.setToAddress(toAddress);
        transaction.setHashId(request.getData().getTxHash());
        transaction.setTimestamp(Instant.now());
        transaction.setSystemProcessDate(Instant.now());
        transaction.setNetwork(network);
        transaction.setNetAmount(request.getData().getNetAmount());
        transaction.setNetworkFee(request.getData().getNetworkFee());

        if (request.getData().getExternalTxId() == null) {
            transaction.setExternalId(UUID.randomUUID().toString());
        } else {
            transaction.setExternalId(request.getData().getExternalTxId());
        }
    }

    private void handleFailedTransaction(Transaction transaction, Optional<Wallet> walletSource, Wallet walletDestination, TransactionWebhookRequest request) {
        log.info("Transaction failed");
        if (walletSource.isPresent()
                && TransactionType.WITHDRAW.equals(transaction.getType())
                && ADMIN_EMAIL.equalsIgnoreCase(walletSource.get().getUser().getEmail())
                && transaction.getWallet() != null) {

            if (!transaction.isRefund()) {
                log.info("Transaction fail. Update balance");
                Wallet updateWallet = transaction.getWallet();
                updateWallet.setBalance(updateWallet.getBalance().add(transaction.getAmount()));
                walletRepository.save(walletSource.get());
                transaction.setRefund(true);
            }
        } else if (TransactionType.WITHDRAW.equals(transaction.getType())
                && walletDestination != null
                && ADMIN_EMAIL.equalsIgnoreCase(walletDestination.getUser().getEmail())
                && !"INSUFFICIENT_FUNDS_FOR_FEE".equalsIgnoreCase(request.getData().getSubStatus())) {

            CreateTransactionWithdrawRequestDto requestHotWallet = new CreateTransactionWithdrawRequestDto();
            requestHotWallet.setAssetId(transaction.getCoin().getSymbol());
            requestHotWallet.setAmount(transaction.getAmount());
            requestHotWallet.setAddress(walletDestination.getAddress());
            createTransactionWithdraw(requestHotWallet, walletSource.get().getUser().getEmail(), walletDestination);
        }
        transaction.setSystemConfirmDate(Instant.now());
        transactionRepository.save(transaction);
    }

    private void handleCompleteTransaction(Transaction transaction, Optional<Wallet> walletSource, Wallet walletDestination, Coin coin) {
        log.info("Transaction complete");
        transaction.setSystemConfirmDate(Instant.now());
        transactionRepository.save(transaction);

        if (TransactionType.WITHDRAW.equals(transaction.getType())
                && walletDestination != null
                && ADMIN_EMAIL.equalsIgnoreCase(walletDestination.getUser().getEmail())) {

            BigDecimal pendingBalance = vaultService.getAssetBalance(walletSource.get().getUser().getVaultId(), coin.getSymbol());
            walletSource.get().setPendingBalance(pendingBalance);
            walletSource.get().setBalance(walletSource.get().getBalance().add(transaction.getNetAmount()));
            walletRepository.save(walletSource.get());
        }

        if (TransactionType.DEPOSIT.equals(transaction.getType())) {
            log.info("Send to hot wallet");
            BigDecimal pendingBalance = vaultService.getAssetBalance(walletDestination.getUser().getVaultId(), coin.getSymbol());
            walletDestination.setPendingBalance(pendingBalance);

            LimitToken limitToken = limitTokenRepository.findByCoinId(coin.getId()).orElseThrow(NotFoundException::new);
            if (limitToken.getLimitQuantity().compareTo(pendingBalance) > 0) {
                log.warn("Limit token {} is less than pending balance {}", limitToken.getLimitQuantity(), pendingBalance);
                return;
            }

            CreateTransactionWithdrawRequestDto requestHotWallet = new CreateTransactionWithdrawRequestDto();
            User admin = userRepository.findByEmail(ADMIN_EMAIL);
            Optional<Wallet> walletAdmin = walletRepository.findByUserAndCoin(admin, coin);

            requestHotWallet.setAssetId(transaction.getCoin().getSymbol());
            requestHotWallet.setAmount(walletDestination.getPendingBalance());
            requestHotWallet.setAddress(walletAdmin.get().getAddress());
            createTransactionWithdraw(requestHotWallet, walletDestination.getUser().getEmail(), walletDestination);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createTransactionWithdraw(CreateTransactionWithdrawRequestDto request, String email, Wallet walletSrc) {
        log.info("Creating transaction for from={}, coin={}, amount={}, to={}, address={}, wallet{}", email, request.getAssetId(), request.getAmount(), request.getDestinationVaultId(), request.getAddress(), walletSrc);

        User fromUser = userRepository.findByEmailAndIsEnabledIsTrue(email).orElseThrow(NotFoundException::new);
        Coin coin = coinRepository.findCoinBySymbolAndIsEnabledIsTrue(request.getAssetId()).orElseThrow(NotFoundException::new);
        Optional<Wallet> toUserWallet = walletRepository.findByAddress(request.getAddress());
        Wallet wallet = walletRepository.findByUserAndCoin(fromUser, coin).orElseThrow(NotFoundException::new);

        boolean hasPendingWithdraw = transactionRepository.existsByFromAddressAndStatus(wallet.getAddress(), TransactionStatus.QUEUED);
        if (hasPendingWithdraw) throw new BadRequestException(ERROR_TRANSACTION_WITHDRAW_EXISTED);

        boolean isHotWalletTransfer = ADMIN_EMAIL.equalsIgnoreCase(fromUser.getEmail()) ||
                (toUserWallet.isPresent() && ADMIN_EMAIL.equalsIgnoreCase(toUserWallet.get().getUser().getEmail()));

        if (isHotWalletTransfer) {
            BigDecimal balance = vaultService.getAssetBalance(fromUser.getVaultId(), coin.getSymbol());
            if (balance.compareTo(request.getAmount()) < 0) {
                throw new BadRequestException(ERROR_WALLET_BALANCE_INSUFFICIENT);
            }

            Transaction transaction = buildWithdrawTransaction(walletSrc, coin, request);
            transactionRepository.save(transaction);

            log.info("Transaction queued successfully with externalId: {}", transaction.getExternalId());
        } else {
            BigDecimal balance = wallet.getBalance();
            if (balance.compareTo(request.getAmount()) < 0) throw new BadRequestException(ERROR_WALLET_BALANCE_INSUFFICIENT);

            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
            walletRepository.save(wallet);

            CreateTransactionWithdrawRequestDto requestWithdraw = new CreateTransactionWithdrawRequestDto();
            requestWithdraw.setAssetId(request.getAssetId());
            requestWithdraw.setAmount(request.getAmount());
            requestWithdraw.setAddress(request.getAddress());
            requestWithdraw.setDestinationVaultId(request.getDestinationVaultId());
            createTransactionWithdraw(requestWithdraw, ADMIN_EMAIL, wallet);
        }
    }

    private Transaction buildWithdrawTransaction(Wallet walletSrc, Coin coin, CreateTransactionWithdrawRequestDto request) {
        Transaction transaction = new Transaction();
        transaction.setCoin(coin);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.QUEUED);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setWallet(walletSrc);
        transaction.setFromAddress(walletSrc.getAddress());
        transaction.setToAddress(request.getAddress());
        transaction.setExternalId(UUID.randomUUID().toString());
        transaction.setTimestamp(Instant.now());
        transaction.setSystemProcessDate(Instant.now());
        transaction.setNetwork(walletSrc.getNetwork());
        return transaction;
    }

    @Override
    @Transactional
    public List<Transaction> getPendingTransactions(String type) {
        if ("verify".equalsIgnoreCase(type)) {
            List<Transaction> transactions = transactionRepository.findByStatus(TransactionStatus.CREATED);
            transactions.addAll(transactionRepository.findByStatus(TransactionStatus.COMFIRMING));
            return transactions;
        }
        return transactionRepository.findByStatus(TransactionStatus.QUEUED);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    public void updateTransaction(Long id) {
        log.info("Process transaction id: {}", id);

        transactionRepository.lockById(id).filter(Transaction::isPending).ifPresentOrElse(transaction -> {
            log.info("Transaction found: {}", transaction);
            TransactionResponse response = fireblocksTransactionService.getTransactionById(transaction.getExternalId(), false);
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
                log.error("JSON parse error while updating transaction", e);
            }

            log.info("Transaction processed successfully: {}", transaction);
        }, () -> log.error("Transaction not found or already processed: {}", id));
    }
}
