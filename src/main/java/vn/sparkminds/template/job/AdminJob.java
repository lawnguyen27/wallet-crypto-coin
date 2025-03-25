package vn.sparkminds.template.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.Network;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.repository.CoinRepository;
import vn.sparkminds.template.repository.NetworkRepository;
import vn.sparkminds.template.repository.UserRepository;
import vn.sparkminds.template.repository.WalletRepository;
import vn.sparkminds.template.service.FireblocksService;
import vn.sparkminds.template.service.UserService;

import java.util.List;

import static vn.sparkminds.template.constant.FireblockConstants.ADMIN_EMAIL;

@Component
@AllArgsConstructor
@Slf4j
public class AdminJob {
    private final FireblocksService fireblocksService;
    private final UserRepository userRepository;
    private final CoinRepository coinRepository;
    private final NetworkRepository networkRepository;
    private final UserService userService;
    private final WalletRepository walletRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAdminVaultAndWallet(){

        User user = userRepository.findByEmail(ADMIN_EMAIL);

        if(user.getVaultId() == null) {
            String vaultId = fireblocksService.createVaultAccount("Hot Wallet");
            user.setVaultId(vaultId);
            userRepository.save(user);
        }
            String vaultId = user.getVaultId();
            List<Coin> coins = coinRepository.findCoinsByIsEnabledIsTrue();
            for (Coin coin : coins) {
                if(walletRepository.findByUserAndCoin(user, coin) != null){
                    continue;
                }
                Network network;
                if (coin.getSymbol().equalsIgnoreCase("SOL_TEST")) {
                    network = networkRepository.findByNetworkType("SOLANA");
                } else {
                    network = networkRepository.findByNetworkType("ETHEREUM");
                }
                userService.createWalletAndNetwork(user, vaultId, coin.getSymbol(), network);
            }
        }

    }

