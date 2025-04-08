package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import vn.sparkminds.template.config.jpa.specifications.QueryService;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.Coin_;
import vn.sparkminds.template.repository.CoinRepository;
import vn.sparkminds.template.service.CoinQueryService;
import vn.sparkminds.template.service.CoinService;
import vn.sparkminds.template.service.criteria.CoinCriteria;
import vn.sparkminds.template.service.dto.CoinDto;
import vn.sparkminds.template.service.mapper.CoinMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinQueryServiceImpl extends QueryService<Coin> implements CoinQueryService {

    /** Repositories **/
    private final CoinRepository coinRepository;

    /** Services **/
    private final CoinService coinService;

    /** Mappers **/
    private final CoinMapper coinMapper;

    /** Others **/

    @Override
    public Page<CoinDto> getCoins(CoinCriteria criteria, Pageable pageable) {
        final Specification<Coin> specification = buildSpecification(criteria);
        return coinRepository.findAll(specification, pageable).map(coinMapper::toDto);
    }

    @Override
    public Page<CoinDto> getCoinsEnable(CoinCriteria criteria, Pageable pageable) {
        final Specification<Coin> specification = buildSpecification(criteria)
                .and((root, query, cb) -> cb.equal(root.get("isEnabled"), true));
        return coinRepository.findAll(specification, pageable).map(coinMapper::toDto);
    }

    @Override
    public List<CoinDto> getCoins(CoinCriteria criteria) {
        final Specification<Coin> specification = buildSpecification(criteria);
        return coinRepository.findAll(specification).stream().map(coinMapper::toDto).toList();
    }

    @Override
    public Optional<CoinDto> getCoin(Long id) {
        return coinRepository.findById(id).map(coinMapper::toDto);
    }

    private Specification<Coin> buildSpecification(CoinCriteria criteria) {
        Specification<Coin> specification = Specification.where(null);

        if (Objects.isNull(criteria)) {
            return specification;
        }

        if (Objects.nonNull(criteria.getId())) {
            specification = specification.and(buildRangeSpecification(criteria.getId(), Coin_.id));
        }

        if (Objects.nonNull(criteria.getSymbol())) {
            specification = specification.and(buildStringSpecification(criteria.getSymbol(), Coin_.symbol));
        }

        if (Objects.nonNull(criteria.getName())) {
            specification = specification.and(buildStringSpecification(criteria.getName(), Coin_.name));
        }

        if (Objects.nonNull(criteria.getLogoUrl())) {
            specification = specification.and(buildStringSpecification(criteria.getLogoUrl(), Coin_.logoUrl));
        }

        return specification;
    }

}
