package vn.sparkminds.template.controller.commons;

import com.fireblocks.sdk.ApiException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.sparkminds.template.repository.UserRepository;
import vn.sparkminds.template.service.AuthenticationService;
import vn.sparkminds.template.service.UserService;
import vn.sparkminds.template.service.dto.LoginResultDto;
import vn.sparkminds.template.service.dto.request.LoginRequestDto;
import vn.sparkminds.template.service.dto.request.RegisterRequestDto;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/common/v1")
@RequiredArgsConstructor
public class AuthenticationCommonController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResultDto> login(@Valid @RequestBody LoginRequestDto request) {

        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterRequestDto> registerUser(@RequestBody RegisterRequestDto request) {

        userService.registerUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authenticationService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
