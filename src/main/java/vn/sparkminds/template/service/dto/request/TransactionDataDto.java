package vn.sparkminds.template.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDataDto {

    private String id;

    private String status;

    private String assetId;

    private String sourceAddress;

    private String destinationAddress;

    private BigDecimal amount;

    private String txHash;

    private TransactionDataSourceDto source;

    private TransactionDataDestinationDto destination;

    private String externalTxId;
}

