package vn.sparkminds.template.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

import static vn.sparkminds.template.constant.ErrorConstants.ERROR_ADDRESS_REQUIRED;
import static vn.sparkminds.template.constant.ErrorConstants.ERROR_ASSET_ID_REQUIRED;

@Data
public class CreateTransactionWithdrawRequestDto {
    @NotBlank(message = ERROR_ASSET_ID_REQUIRED)
    private String assetId;

    private String address;

    private String destinationVaultId;

    private BigDecimal amount;
}
