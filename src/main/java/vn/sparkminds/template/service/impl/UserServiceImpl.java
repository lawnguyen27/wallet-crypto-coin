package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.error.http.NotFoundException;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.Network;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.entity.enumeration.UserRole;
import vn.sparkminds.template.repository.CoinRepository;
import vn.sparkminds.template.repository.NetworkRepository;
import vn.sparkminds.template.repository.UserRepository;
import vn.sparkminds.template.repository.WalletRepository;
import vn.sparkminds.template.service.FireblocksService;
import vn.sparkminds.template.service.UserService;
import vn.sparkminds.template.service.dto.request.RegisterRequestDto;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;
    private final FireblocksService fireblocksService;
    private final PasswordEncoder passwordEncoder;
    private final NetworkRepository networkRepository;

    @Transactional
    @Override
    public RegisterRequestDto registerUser(RegisterRequestDto request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        user.setIsEnabled(true);
        userRepository.save(user);

        String vaultId = fireblocksService.createVaultAccount(user.getEmail());
        user.setVaultId(vaultId);
        userRepository.save(user);

        List<Coin> coins = coinRepository.findCoinsByIsEnabledIsTrue();
        for (Coin coin : coins) {
            Network network;
            if (coin.getSymbol().equalsIgnoreCase("SOL_TEST")) {
                network = networkRepository.findByNetworkType("SOLANA");
            } else {
                network = networkRepository.findByNetworkType("ETHEREUM");
            }
            createWalletAndNetwork(user, vaultId, coin.getSymbol(), network);
        }
        return request;
    }

    @Override
    public void createWalletAndNetwork(User user, String vaultId, String coinSymbol, Network network) {

        String address = fireblocksService.createWalletAddresses(vaultId, coinSymbol);
        log.info("wallet address {}, network", address, network.getNetworkType());

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCoin(coinRepository.findCoinBySymbol(coinSymbol));
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setAddress(address);
        wallet.setNetwork(network);
        walletRepository.save(wallet);
    }

    @Override
    public void blockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        user.setIsEnabled(false);
        userRepository.save(user);
    }
}

