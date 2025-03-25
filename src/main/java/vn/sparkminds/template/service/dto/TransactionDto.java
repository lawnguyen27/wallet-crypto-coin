package vn.sparkminds.template.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.sparkminds.template.entity.enumeration.TransactionStatus;
import vn.sparkminds.template.entity.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private String hashId;
    private String fromAddress;
    private String toAddress;
    private BigDecimal amount;
    private TransactionStatus status;
    private Instant timestamp;
    private Instant systemProcessDate;
    private Instant systemConfirmDate;
    private String externalId;
    private String fireblockTransactionId;
    private TransactionType type;
    private Long coinId;
    private Long networkId;
}