package vn.sparkminds.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.sparkminds.template.service.criteria.CoinCriteria;
import vn.sparkminds.template.service.dto.CoinDto;

import java.util.List;
import java.util.Optional;

public interface CoinQueryService {

    Page<CoinDto> getCoins(CoinCriteria criteria, Pageable pageable);

    List<CoinDto> getCoins(CoinCriteria criteria);

    Optional<CoinDto> getCoin(Long id);

    Page<CoinDto> getCoinsEnable(CoinCriteria criteria, Pageable pageable);

}
