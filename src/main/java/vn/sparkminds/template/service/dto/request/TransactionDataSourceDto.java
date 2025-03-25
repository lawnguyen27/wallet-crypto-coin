package vn.sparkminds.template.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDataSourceDto {
    private String id;
    private String type;
}
