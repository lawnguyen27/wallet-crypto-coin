package vn.sparkminds.template.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.sparkminds.template.config.ApplicationProperties;
import vn.sparkminds.template.config.error.http.BadRequestException;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.repository.UserRepository;
import vn.sparkminds.template.service.AuthenticationService;
import vn.sparkminds.template.service.dto.LoginResultDto;
import vn.sparkminds.template.service.dto.request.LoginRequestDto;
import vn.sparkminds.template.util.CollectionUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ApplicationProperties properties;
    private final UserRepository userRepository;
    private final ObjectProvider<PasswordEncoder> passwordEncoder;
    private final StringRedisTemplate redisTemplate; // Redis Template

    @Override
    public LoginResultDto login(LoginRequestDto request) {
        final Map<String, Object> claims = new HashMap<>();

        final User user = userRepository.findUserByEmailAndIsEnabledIsTrue(request.getEmail());
        log.info("User: {}", user);

        if (user != null && passwordEncoder.getIfAvailable().matches(request.getPassword(), user.getPassword())) {

            claims.put("role", user.getRole());
            return new LoginResultDto(generateToken(request.getEmail(), claims));

        } else throw new BadRequestException("Invalid email or password");
    }

    @Override
    public void logout(String token) {
        long expiration = getExpirationFromToken(token);
        if (expiration > 0) {
            log.info("Revoking token: {} for {} seconds", token, expiration);
            redisTemplate.opsForValue().set("revoked_token:" + token, "true", expiration, TimeUnit.SECONDS);
        }
    }

    @Override
    public Optional<Authentication> authenticate(String token) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey("revoked_token:" + token))) {
                log.warn("Token is revoked: {}", token);
                return Optional.empty();
            }
            final SecretKey key = Keys.hmacShaKeyFor(properties.getSecurity().getJwtSecret().getBytes(StandardCharsets.UTF_8));
            final Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

            final List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            authorities.add(new SimpleGrantedAuthority(claims.get("role").toString()));

            if (Objects.nonNull(claims.get("authorities"))) {
                CollectionUtils.convertToListString(claims.get("authorities")).forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));
            }

            return Optional.of(new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private String generateToken(String email, Map<String, Object> claims) {
        final Key key = Keys.hmacShaKeyFor(properties.getSecurity().getJwtSecret().getBytes(StandardCharsets.UTF_8));
        final long expiration = properties.getSecurity().getJwtExpirationInSeconds() * 1000;

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .claims(claims)
                .compact();
    }

    private long getExpirationFromToken(String token) {
        try {
            final SecretKey key = Keys.hmacShaKeyFor(properties.getSecurity().getJwtSecret().getBytes(StandardCharsets.UTF_8));
            final Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Date expiration = claims.getExpiration();
            return (expiration.getTime() - System.currentTimeMillis()) / 1000; // Convert to seconds
        } catch (Exception e) {
            return 0;
        }
    }

}
