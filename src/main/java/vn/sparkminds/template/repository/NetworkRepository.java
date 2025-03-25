package vn.sparkminds.template.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.Network;

import java.util.List;
import java.util.Optional;

public interface NetworkRepository extends JpaRepository<Network, Long>, JpaSpecificationExecutor<Network> {

    @Query("SELECT n FROM Network n JOIN n.wallets w WHERE w.id = :walletId")
    Optional<Network> findNetworkByWalletId(@Param("walletId") Long walletId);

    Network findByNetworkType(String name);
}
