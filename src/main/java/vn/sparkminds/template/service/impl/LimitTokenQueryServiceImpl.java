package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.jpa.specifications.QueryService;
import vn.sparkminds.template.entity.LimitToken;
import vn.sparkminds.template.entity.LimitToken_;
import vn.sparkminds.template.repository.LimitTokenRepository;
import vn.sparkminds.template.service.LimitTokenQueryService;
import vn.sparkminds.template.service.criteria.LimitTokenCriteria;
import vn.sparkminds.template.service.dto.LimitTokenDto;
import vn.sparkminds.template.service.mapper.LimitTokenMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LimitTokenQueryServiceImpl extends QueryService<LimitToken> implements LimitTokenQueryService {

    private final LimitTokenRepository limitTokenRepository;

    private final LimitTokenMapper limitTokenMapper;

    @Override
    public Page<LimitTokenDto> getLimitTokens(LimitTokenCriteria criteria, Pageable pageable) {
        final Specification<LimitToken> specification = buildSpecification(criteria);
        return limitTokenRepository.findAll(specification, pageable).map(limitTokenMapper::toDto);
    }

    @Override
    public List<LimitTokenDto> getLimitTokens(LimitTokenCriteria criteria) {
        final Specification<LimitToken> specification = buildSpecification(criteria);
        return limitTokenRepository.findAll(specification).stream().map(limitTokenMapper::toDto).toList();
    }

    @Override
    public Optional<LimitTokenDto> getLimitToken(Long id) {
        return limitTokenRepository.findById(id).map(limitTokenMapper::toDto);
    }

    private Specification<LimitToken> buildSpecification(LimitTokenCriteria criteria) {
        Specification<LimitToken> specification = Specification.where(null);

        if (Objects.isNull(criteria)) {
            return specification;
        }

        if (Objects.nonNull(criteria.getId())) {
            specification = specification.and(buildRangeSpecification(criteria.getId(), LimitToken_.id));
        }

        if (Objects.nonNull(criteria.getLimitQuantity())) {
            specification = specification.and(buildRangeSpecification(criteria.getLimitQuantity(), LimitToken_.limitQuantity));
        }

        if (Objects.nonNull(criteria.getCoinId())) {
            specification = specification.and(buildSpecification(criteria.getCoinId(), root -> root.join(LimitToken_.coin).get("id")));
        }

        return specification;
    }
}
