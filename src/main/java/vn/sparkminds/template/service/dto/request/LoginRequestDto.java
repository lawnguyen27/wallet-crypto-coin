package vn.sparkminds.template.service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static vn.sparkminds.template.constant.ErrorConstants.ERROR_EMAIL_INVALID;
import static vn.sparkminds.template.constant.ErrorConstants.ERROR_EMAIL_REQUIRED;
import static vn.sparkminds.template.constant.ErrorConstants.ERROR_PASSWORD_REQUIRED;

@Data
public class LoginRequestDto {

    @NotBlank(message = ERROR_EMAIL_REQUIRED)
    @Email(message = ERROR_EMAIL_INVALID)
    private String email;

    @NotBlank(message = ERROR_PASSWORD_REQUIRED)
    private String password;

}
