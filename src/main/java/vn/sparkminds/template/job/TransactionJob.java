package vn.sparkminds.template.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.entity.enumeration.TransactionStatus;
import vn.sparkminds.template.repository.TransactionRepository;
import vn.sparkminds.template.repository.WalletRepository;
import vn.sparkminds.template.service.FireblocksTransactionService;
import vn.sparkminds.template.service.TransactionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionJob {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final FireblocksTransactionService fireblocksTransactionService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void processTransactions() {
        log.info("Start processing transactions");

        transactionService.getPendingTransactions("verify").forEach(transaction -> {
            log.info("Processing transaction: {}", transaction.getId());
            transactionService.updateTransaction(transaction.getId());
        });

        log.info("Finish processing transactions");
    }

    @Scheduled(cron = "*/5 * * * * ?")
    @Transactional
    public void sendWithdrawTransactions() {

        // get list transaction QUEUED
        transactionService.getPendingTransactions("withdraw").forEach(transaction -> {
            log.info("Processing transaction: {}", transaction.getId());
            try {
                // send request to Fireblocks
                User fromUser = walletRepository.findByAddress(transaction.getFromAddress())
                        .map(Wallet::getUser)
                        .orElse(null);
                User toUser = walletRepository.findByAddress(transaction.getToAddress())
                        .map(Wallet::getUser)
                        .orElse(null);
                fireblocksTransactionService.createTransaction(
                        fromUser,
                        transaction.getCoin(),
                        transaction.getAmount(),
                        toUser,
                        transaction.getToAddress(),
                        transaction.getExternalId());
//                // check fee
//                    BigDecimal feeAmount = new BigDecimal(fee);
//                    if (feeAmount.compareTo(transaction.getAmount())>0){
//                        log.error("Error processing transaction {}: {}", transaction.getId(), "Fee is greater than amount");
//                        return;
//                    }

                transaction.setStatus(TransactionStatus.COMFIRMING);
                transactionRepository.save(transaction);
            } catch (Exception ex) {
                log.error("Error processing transaction {}: {}", transaction.getId(), ex.getMessage());
            }
        });
    }
}

