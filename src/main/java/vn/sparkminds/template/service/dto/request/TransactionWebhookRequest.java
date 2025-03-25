package vn.sparkminds.template.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionWebhookRequest {

    private String type;

    private String tenantId;

    private Long timestamp;

    private TransactionDataDto data;

}

