package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.Network;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.repository.CoinRepository;
import vn.sparkminds.template.repository.WalletRepository;
import vn.sparkminds.template.service.NetworkService;
import vn.sparkminds.template.service.VaultService;
import vn.sparkminds.template.service.WalletService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {
    private final CoinRepository coinRepository;
    private final WalletRepository walletRepository;
    private final VaultService vaultService;
    private final NetworkService networkService;

    @Override
    public void createWalletsForUser(User user, String vaultId) {
        List<Coin> coins = coinRepository.findCoinsByIsEnabledIsTrue();
        for (Coin coin : coins) {
            Network network = networkService.resolveNetworkByCoin(coin);
            createWallet(user, coin, network, vaultId);
        }
    }

    private void createWallet(User user, Coin coin, Network network, String vaultId) {
        String address = vaultService.createWalletAddresses(vaultId, coin.getSymbol());
        log.info("wallet address: {}, network: {}", address, network.getNetworkType());

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCoin(coin);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setAddress(address);
        wallet.setNetwork(network);
        walletRepository.save(wallet);
    }
}
