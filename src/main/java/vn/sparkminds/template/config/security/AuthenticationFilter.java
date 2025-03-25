package vn.sparkminds.template.config.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.sparkminds.template.service.AuthenticationService;
import vn.sparkminds.template.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {

        extractTokenFromHeader(request).ifPresent(token -> {
            final Optional<Authentication> authentication = authenticationService.authenticate(token);
            authentication.ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");

        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.of(authorization.substring(7));
    }

}
