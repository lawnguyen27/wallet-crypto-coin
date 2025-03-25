package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.jpa.specifications.QueryService;
import vn.sparkminds.template.entity.Network;
import vn.sparkminds.template.entity.Network_;
import vn.sparkminds.template.repository.NetworkRepository;
import vn.sparkminds.template.service.NetworkQueryService;
import vn.sparkminds.template.service.criteria.NetworkCriteria;
import vn.sparkminds.template.service.dto.NetworkDto;
import vn.sparkminds.template.service.mapper.NetworkMapper;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NetworkQueryServiceImpl extends QueryService<Network> implements NetworkQueryService {

    private final NetworkRepository networkRepository;
    private final NetworkMapper networkMapper;

    @Override
    public Page<NetworkDto> getNetworks(NetworkCriteria criteria, Pageable pageable) {
        final Specification<Network> specification = buildSpecification(criteria);
        return networkRepository.findAll(specification, pageable).map(networkMapper::toDto);
    }

    @Override
    public List<NetworkDto> getNetworks(NetworkCriteria criteria) {
        final Specification<Network> specification = buildSpecification(criteria);
        return networkRepository.findAll(specification).stream().map(networkMapper::toDto).toList();
    }

    private Specification<Network> buildSpecification(NetworkCriteria criteria) {
        Specification<Network> specification = Specification.where(null);

        if (Objects.isNull(criteria)) {
            return specification;
        }

        if (Objects.nonNull(criteria.getId())) {
            specification = specification.and(buildRangeSpecification(criteria.getId(), Network_.id));
        }

        if (Objects.nonNull(criteria.getName())) {
            specification = specification.and(buildStringSpecification(criteria.getName(), Network_.name));
        }

        if (Objects.nonNull(criteria.getNetworkType())) {
            specification = specification.and(buildStringSpecification(criteria.getNetworkType(), Network_.networkType));
        }

        return specification;
    }


}

