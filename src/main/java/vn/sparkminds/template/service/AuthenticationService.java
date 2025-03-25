package vn.sparkminds.template.service;

import org.springframework.security.core.Authentication;
import vn.sparkminds.template.service.dto.LoginResultDto;
import vn.sparkminds.template.service.dto.request.LoginRequestDto;

import java.util.Optional;

public interface AuthenticationService {

    LoginResultDto login(LoginRequestDto request);

    Optional<Authentication> authenticate(String token);

    void logout(String token);
}
