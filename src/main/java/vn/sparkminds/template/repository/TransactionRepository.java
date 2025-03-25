package vn.sparkminds.template.repository;

import feign.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.sparkminds.template.entity.Transaction;
import vn.sparkminds.template.entity.enumeration.TransactionStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transaction t WHERE t.id = :id")
    Optional<Transaction> lockById(Long id);

    List<Transaction> findByStatus(TransactionStatus status);

    Transaction findByFireblockTransactionId(String fireblockTransactionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transaction t WHERE t.fireblockTransactionId = :id")
    Transaction findByFireblockTransactionIdWithLock(@Param("id") String id);

    boolean existsByFromAddressAndStatus(String fromAddress, TransactionStatus status);

    Transaction findByExternalId(String externalId);
}
