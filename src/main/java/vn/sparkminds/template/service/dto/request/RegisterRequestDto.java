package vn.sparkminds.template.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static vn.sparkminds.template.constant.ErrorConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequestDto {
    @NotBlank()
    @Size()
    private String name;

    @NotBlank(message = ERROR_EMAIL_INVALID)
    @Email(message = ERROR_EMAIL_INVALID)
    private String email;

    @NotBlank(message = ERROR_PASSWORD_REQUIRED)
    @Size()
    private String password;
}
