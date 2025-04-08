package vn.sparkminds.template.service.impl;

import com.fireblocks.sdk.ApiResponse;
import com.fireblocks.sdk.ConfigurationOptions;
import com.fireblocks.sdk.Fireblocks;
import com.fireblocks.sdk.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.sparkminds.template.config.fireblock.FireblocksConfig;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.service.FireblocksTransactionService;
import vn.sparkminds.template.service.VaultService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FireblocksTransactionServiceImpl implements FireblocksTransactionService, VaultService {

    private final FireblocksConfig config;
    private Fireblocks fireblocks;

    private Fireblocks getFireblocksInstance() {

        if (fireblocks == null) {
            ConfigurationOptions configOptions = new ConfigurationOptions();
            configOptions.apiKey(config.getApiKey());
            configOptions.secretKey(config.getSecretKey());
            configOptions.basePath(config.getBaseUrl());
            fireblocks = new Fireblocks(configOptions);
        }

        return fireblocks;
    }

    @Override
    public String createVaultAccount(String email) {
        try {
            log.info("Create vault account for userId={}", email);

            String idempotencyKey = UUID.randomUUID().toString();
            Fireblocks fireblocks = getFireblocksInstance();

            CreateVaultAccountRequest request = new CreateVaultAccountRequest();
            request.setName("User_" + email);
            request.setHiddenOnUI(false);

            var response = fireblocks
                    .vaults()
                    .createVaultAccount(request, idempotencyKey);

            return response.get().getData().getId();

        } catch (Exception e) {
            log.error("Create Vault Account fail: {}", e.getMessage(), e);
            throw new RuntimeException("Can not create Vault Account", e);
        }
    }

    @Override
    public String createWalletAddresses(String vaultId, String coin) {
        try {


            log.info("Create vault addrees for vaultId={} with coins={}", vaultId, coin);
            String idempotencyKey = UUID.randomUUID().toString();
            Fireblocks fireblocks = getFireblocksInstance();
            var response = fireblocks.vaults().activateAssetForVaultAccount(vaultId, coin, idempotencyKey);
            String address = response.get().getData().getAddress();

            log.info("Created address {} for coin {}", address, coin);

            return address;

        } catch (Exception e) {
            log.error("Create address fail: {}", e.getMessage(), e);
            throw new RuntimeException("Can not create address", e);
        }
    }

    @Override
    public boolean verifySignature(String requestBody, String signature) {
        try {
            PublicKey publicKey = config.getPublicKey();
            Signature sig = Signature.getInstance("SHA512withRSA");
            sig.initVerify(publicKey);
            sig.update(requestBody.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            boolean isValid = sig.verify(signatureBytes);

            if (!isValid) {
                log.warn("Fireblocks signature verification failed!");
                log.warn("Signature Base64: {}", signature);
            }

            return isValid;

        } catch (Exception e) {
            log.error("Error verifying Fireblocks signature: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public BigDecimal getAssetBalance(String vaultId, String assetId) {
        try {
            log.info("Fetching asset balance for vaultId={} and assetId={}", vaultId, assetId);

            Fireblocks fireblocks = getFireblocksInstance();


            var response = fireblocks
                    .vaults()
                    .getVaultAccountAsset(vaultId, assetId)
                    .get();

            if (response != null && response.getData() != null) {
                String balanceStr = response.getData().getTotal();
                BigDecimal balance = new BigDecimal(balanceStr);

                log.info("Asset balance for vault {} and asset {}: {}", vaultId, assetId, balance);
                return balance;
            } else {
                log.warn("No balance found for vault {} and asset {}", vaultId, assetId);
                return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            log.error("Failed to fetch asset balance: {}", e.getMessage(), e);
            throw new RuntimeException("Cannot fetch asset balance", e);
        }
    }

    @Override
    public void createTransaction(User fromUser, Coin coin, BigDecimal amount, User toUser, String address, String externalTxId) {
        try {
            Fireblocks fireblocks = getFireblocksInstance();

            String idempotencyKey = UUID.randomUUID().toString();

            log.info("Sending transaction to Fireblocks: from{}, coin={}, amount={}, to{}",
                    fromUser, coin.getSymbol(), amount, (toUser != null ? toUser : address));

            SourceTransferPeerPath source = new SourceTransferPeerPath();
            source.setType(TransferPeerPathType.VAULT_ACCOUNT);
            source.setId(fromUser.getVaultId());

            DestinationTransferPeerPath destination = new DestinationTransferPeerPath();
            if (toUser != null) {
                destination.setType(TransferPeerPathType.VAULT_ACCOUNT);
                destination.setId(toUser.getVaultId());
            } else if (address != null) {
                destination.setType(TransferPeerPathType.ONE_TIME_ADDRESS);
                OneTimeAddress oneTimeAddress = new OneTimeAddress();
                oneTimeAddress.setAddress(address);
                destination.setOneTimeAddress(oneTimeAddress);
            } else {
                throw new IllegalArgumentException("Either toUser or address must be provided");
            }

            TransactionRequest request = new TransactionRequest();
            request.setSource(source);
            request.setAssetId(coin.getSymbol());
            request.setAmount(new TransactionRequestAmount(amount));
            request.setDestination(destination);
            request.setExternalTxId(externalTxId);
            // 🔹 Gửi transaction qua Fireblocks
            var estimateFee =fireblocks.transactions().estimateTransactionFee(request, idempotencyKey);
           //check fee
            BigDecimal fee = new BigDecimal(estimateFee.get().getData().getMedium().getNetworkFee());

            var response = fireblocks.transactions().createTransaction(request, null, idempotencyKey);
            log.info("Estimate fee for coin {} is {}", coin.getSymbol(), fee);

        } catch (Exception e) {
            log.error("Fireblocks transaction failed: {}", e.getMessage(), e);
            throw new RuntimeException("Transaction failed", e);
        }
    }

    @Override
    public TransactionResponse getTransactionById(String transactionId, boolean isExternalId) {
        try {
            log.info("Fetching transaction details for transactionId={}", transactionId);

            Fireblocks fireblocks = getFireblocksInstance();

            CompletableFuture<ApiResponse<TransactionResponse>> response = null;
            if (isExternalId == false) {
                response = fireblocks
                        .transactions()
                        .getTransaction(transactionId);
            } else {
                response = fireblocks
                        .transactions()
                        .getTransactionByExternalId(transactionId);
            }

            if (response.get() != null && response.get().getData() != null) {
                log.info("Get transaction details success");
                return response.get().getData();

            } else {
                log.warn("Transaction not found for transactionId={}", transactionId);
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to fetch transaction details: {}", e.getMessage(), e);
            return null;
        }
    }
}

