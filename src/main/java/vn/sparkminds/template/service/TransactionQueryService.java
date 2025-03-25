package vn.sparkminds.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.sparkminds.template.service.criteria.TransactionCriteria;
import vn.sparkminds.template.service.dto.TransactionDto;

import java.util.List;
import java.util.Optional;

public interface TransactionQueryService {

    Page<TransactionDto> getTransactions(TransactionCriteria criteria, Pageable pageable);

    List<TransactionDto> getTransactions(TransactionCriteria criteria);

    Optional<TransactionDto> getTransaction(Long id);

}
