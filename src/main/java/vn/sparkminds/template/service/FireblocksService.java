package vn.sparkminds.template.service;

import com.fireblocks.sdk.model.TransactionResponse;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.User;

import java.math.BigDecimal;

public interface FireblocksService {

    String createVaultAccount(String email);

    String createWalletAddresses(String vaultId, String coin);

    boolean verifySignature(String requestBody, String signature);

    BigDecimal getAssetBalance(String vaultId, String assetId);

    void createTransaction(User fromUser, Coin coin, BigDecimal amount, User toUser, String address, String externalTxId);

    TransactionResponse getTransactionById(String transactionId, boolean isExternalId);


}
