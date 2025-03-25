package vn.sparkminds.template.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static vn.sparkminds.template.constant.ErrorConstants.ERROR_COIN_LOGO_URL_LENGTH;
import static vn.sparkminds.template.constant.ErrorConstants.ERROR_COIN_LOGO_URL_REQUIRED;
import static vn.sparkminds.template.constant.ErrorConstants.ERROR_COIN_NAME_LENGTH;
import static vn.sparkminds.template.constant.ErrorConstants.ERROR_COIN_NAME_REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateCoinRequestDto {

    @NotBlank(message = ERROR_COIN_NAME_REQUIRED)
    @Size(max = 50, message = ERROR_COIN_NAME_LENGTH)
    private String name;

    @NotBlank(message = ERROR_COIN_LOGO_URL_REQUIRED)
    @Size(max = 255, message = ERROR_COIN_LOGO_URL_LENGTH)
    private String logoUrl;

}
