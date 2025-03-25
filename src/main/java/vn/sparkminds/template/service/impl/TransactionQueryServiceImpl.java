package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.jpa.specifications.QueryService;
import vn.sparkminds.template.entity.Coin_;
import vn.sparkminds.template.entity.Transaction;
import vn.sparkminds.template.entity.Transaction_;
import vn.sparkminds.template.repository.TransactionRepository;
import vn.sparkminds.template.service.TransactionQueryService;
import vn.sparkminds.template.service.criteria.TransactionCriteria;
import vn.sparkminds.template.service.dto.TransactionDto;
import vn.sparkminds.template.service.mapper.TransactionMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionQueryServiceImpl extends QueryService<Transaction> implements TransactionQueryService {

    /** Repositories **/
    private final TransactionRepository transactionRepository;

    /** Services **/

    /** Mappers **/
    private final TransactionMapper transactionMapper;

    /** Others **/

    @Override
    public Page<TransactionDto> getTransactions(TransactionCriteria criteria, Pageable pageable) {
        final Specification<Transaction> specification = buildSpecification(criteria);
        return transactionRepository.findAll(specification, pageable).map(transactionMapper::toDto);
    }

    @Override
    public List<TransactionDto> getTransactions(TransactionCriteria criteria) {
        final Specification<Transaction> specification = buildSpecification(criteria);
        return transactionRepository.findAll(specification).stream().map(transactionMapper::toDto).toList();
    }

    @Override
    public Optional<TransactionDto> getTransaction(Long id) {
        return transactionRepository.findById(id).map(transactionMapper::toDto);
    }

    private Specification<Transaction> buildSpecification(TransactionCriteria criteria) {
        Specification<Transaction> specification = Specification.where(null);

        if (criteria == null) {
            return specification;
        }

        if (criteria.getId() != null) {
            specification = specification.and(buildRangeSpecification(criteria.getId(), Transaction_.id));
        }

        if (criteria.getSystemConfirmDate() != null) {
            specification = specification.and(buildRangeSpecification(criteria.getSystemConfirmDate(), Transaction_.systemConfirmDate));
        }

        if (criteria.getSystemProcessDate() != null) {
            specification = specification.and(buildRangeSpecification(criteria.getSystemProcessDate(), Transaction_.systemProcessDate));
        }

        if (criteria.getStatus() != null) {
            specification = specification.and(buildSpecification(criteria.getStatus(), Transaction_.status));
        }

        if (criteria.getWalletId() != null) {
            specification = specification.and(buildSpecification(criteria.getWalletId(), root -> root.get(Transaction_.wallet).get("id")));
        }

        if (criteria.getNetworkId() != null) {
            specification = specification.and(buildSpecification(criteria.getNetworkId(), root -> root.get(Transaction_.network).get("id")));
        }

        if (criteria.getCoinId() != null) {
            specification = specification.and(buildSpecification(criteria.getCoinId(), root -> root.get(Transaction_.coin).get("id")));
        }

        if (criteria.getAmount() != null) {
            specification = specification.and(buildRangeSpecification(criteria.getAmount(), Transaction_.amount));
        }

        return specification;
    }


}
