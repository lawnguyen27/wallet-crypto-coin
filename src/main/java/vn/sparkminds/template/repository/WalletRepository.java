package vn.sparkminds.template.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long>, JpaSpecificationExecutor<Wallet> {
    Optional<Wallet> findByAddress(String address);

    Wallet findByUser_VaultIdAndAndCoin_Symbol(String id, String symbol);

    Optional<Wallet> findWalletByUser_VaultIdAndCoin_Symbol(String id, String symbol);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user = :user AND w.coin = :coin")
    Optional<Wallet> findByUserAndCoin(@Param("user") User user,
                                       @Param("coin") Coin coin);
}
