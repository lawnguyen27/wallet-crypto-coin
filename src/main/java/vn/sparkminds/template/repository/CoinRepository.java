package vn.sparkminds.template.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.sparkminds.template.entity.Coin;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long>, JpaSpecificationExecutor<Coin> {

    Optional<Coin> findByIdAndIsEnabledTrue(Long id);

    Coin findCoinBySymbol(String symbol);

    Optional<Coin> findCoinBySymbolAndIsEnabledIsTrue(String symbol);

    List<Coin> findCoinsByIsEnabledIsTrue();

}
