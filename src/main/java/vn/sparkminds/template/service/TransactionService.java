package vn.sparkminds.template.service;

import vn.sparkminds.template.entity.Transaction;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.service.dto.request.CreateTransactionRequestDto;
import vn.sparkminds.template.service.dto.request.CreateTransactionWithdrawRequestDto;
import vn.sparkminds.template.service.dto.request.TransactionDataDto;
import vn.sparkminds.template.service.dto.request.TransactionWebhookRequest;

import java.util.List;

public interface TransactionService {

    void processTransaction(TransactionWebhookRequest request);

    //void createTransaction(CreateTransactionRequestDto request, String email);

    List<Transaction> getPendingTransactions(String type);

    void updateTransaction(Long id);

    void createTransactionWithdraw(CreateTransactionWithdrawRequestDto request, String email, Wallet wallet);
}
