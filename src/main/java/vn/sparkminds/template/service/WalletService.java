package vn.sparkminds.template.service;

import vn.sparkminds.template.entity.User;

public interface WalletService {
    void createWalletsForUser(User user, String vaultId);
}
