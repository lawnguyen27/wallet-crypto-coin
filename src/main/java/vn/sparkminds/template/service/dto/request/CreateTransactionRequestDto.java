package vn.sparkminds.template.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

import static vn.sparkminds.template.constant.ErrorConstants.*;

@Data
public class CreateTransactionRequestDto {

    @NotBlank(message = ERROR_ASSET_ID_REQUIRED)
    private String assetId;

    @NotBlank(message = ERROR_VAULT_ID_REQUIRED)
    private String destinationVaultId;

    private BigDecimal amount;
}
