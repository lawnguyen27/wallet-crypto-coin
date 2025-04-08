package vn.sparkminds.template.service;

import java.math.BigDecimal;

public interface VaultService {
    String createVaultAccount(String email);

    String createWalletAddresses(String vaultId, String coin);

    BigDecimal getAssetBalance(String vaultId, String assetId);

}
