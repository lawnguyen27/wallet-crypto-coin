package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.jpa.specifications.QueryService;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.entity.Wallet_;
import vn.sparkminds.template.repository.WalletRepository;
import vn.sparkminds.template.service.WalletQueryService;
import vn.sparkminds.template.service.criteria.WalletCriteria;
import vn.sparkminds.template.service.dto.WalletDto;
import vn.sparkminds.template.service.mapper.WalletMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletQueryServiceImpl extends QueryService<Wallet> implements WalletQueryService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    public Page<WalletDto> getWallets(WalletCriteria criteria, Pageable pageable) {
        final Specification<Wallet> specification = buildSpecification(criteria);
        return walletRepository.findAll(specification, pageable).map(walletMapper::toDto);
    }

    @Override
    public Page<WalletDto> getWallets(WalletCriteria criteria, Pageable pageable, String email) {
        Specification<Wallet> specification = buildSpecification(criteria)
                .and((root, query, cb) -> cb.equal(root.get("user").get("email"), email));

        return walletRepository.findAll(specification, pageable).map(walletMapper::toDto);
    }

    @Override
    public List<WalletDto> getWallets(WalletCriteria criteria) {
        final Specification<Wallet> specification = buildSpecification(criteria);
        return walletRepository.findAll(specification).stream().map(walletMapper::toDto).toList();
    }

    @Override
    public Optional<WalletDto> getWallet(Long id) {
        return walletRepository.findById(id).map(walletMapper::toDto);
    }

    private Specification<Wallet> buildSpecification(WalletCriteria criteria) {
        Specification<Wallet> specification = Specification.where(null);

        if (Objects.isNull(criteria)) {
            return specification;
        }

        if (Objects.nonNull(criteria.getId())) {
            specification = specification.and(buildRangeSpecification(criteria.getId(), Wallet_.id));
        }

        if (Objects.nonNull(criteria.getBalance())) {
            specification = specification.and(buildRangeSpecification(criteria.getBalance(), Wallet_.balance));
        }

        if (Objects.nonNull(criteria.getPendingBalance())) {
            specification = specification.and(buildRangeSpecification(criteria.getBalance(), Wallet_.pendingBalance));
        }

        if (Objects.nonNull(criteria.getAddress())) {
            specification = specification.and(buildSpecification(criteria.getAddress(), Wallet_.address));
        }

        if (Objects.nonNull(criteria.getUserId())) {
            specification = specification.and(buildSpecification(criteria.getUserId(), root -> root.get(Wallet_.user).get("id")));
        }

        if (Objects.nonNull(criteria.getCoinId())) {
            specification = specification.and(buildSpecification(criteria.getCoinId(), root -> root.get(Wallet_.coin).get("id")));
        }

        if (Objects.nonNull(criteria.getNetworkId())) {
            specification = specification.and(buildSpecification(criteria.getNetworkId(), root -> root.get(Wallet_.network).get("id")));
        }
        return specification;
    }
}
