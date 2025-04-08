package vn.sparkminds.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.sparkminds.template.entity.LimitToken;
import vn.sparkminds.template.entity.Network;

import java.util.Optional;

public interface LimitTokenRepository extends JpaRepository<LimitToken, Long>, JpaSpecificationExecutor<LimitToken> {
    Optional<LimitToken> findByCoinId(Long coinId);
}
