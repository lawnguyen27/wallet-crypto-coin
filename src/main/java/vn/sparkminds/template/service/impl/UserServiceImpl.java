package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.error.http.NotFoundException;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.repository.CoinRepository;
import vn.sparkminds.template.repository.NetworkRepository;
import vn.sparkminds.template.repository.UserRepository;
import vn.sparkminds.template.repository.WalletRepository;
import vn.sparkminds.template.service.UserService;
import vn.sparkminds.template.service.VaultService;
import vn.sparkminds.template.service.WalletService;
import vn.sparkminds.template.service.dto.request.RegisterRequestDto;
import vn.sparkminds.template.service.mapper.UserMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VaultService vaultService;
    private final UserMapper userMapper;
    private final WalletService walletService;

    @Transactional
    @Override
    public RegisterRequestDto registerUser(RegisterRequestDto request) {

        User user = userMapper.fromRegisterDto(request);
        userRepository.save(user);

        String vaultId = vaultService.createVaultAccount(user.getEmail());
        user.setVaultId(vaultId);
        userRepository.save(user);

        walletService.createWalletsForUser(user, vaultId);
        return request;
    }

    @Override
    public void blockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        user.setIsEnabled(false);
        userRepository.save(user);
    }
}

