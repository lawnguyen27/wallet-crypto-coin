package vn.sparkminds.template.service;

import com.fireblocks.sdk.model.TransactionResponse;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.User;

import java.math.BigDecimal;

public interface FireblocksTransactionService {

    boolean verifySignature(String requestBody, String signature);

    void createTransaction(User fromUser, Coin coin, BigDecimal amount, User toUser, String address, String externalTxId);

    TransactionResponse getTransactionById(String transactionId, boolean isExternalId);

}
