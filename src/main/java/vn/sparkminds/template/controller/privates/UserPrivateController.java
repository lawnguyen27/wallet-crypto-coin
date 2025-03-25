package vn.sparkminds.template.controller.privates;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.sparkminds.template.service.CoinQueryService;
import vn.sparkminds.template.service.CoinService;
import vn.sparkminds.template.service.UserQueryService;
import vn.sparkminds.template.service.UserService;
import vn.sparkminds.template.service.criteria.UserCriteria;
import vn.sparkminds.template.service.dto.UserDto;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/private/v1/users")
@RequiredArgsConstructor
public class UserPrivateController {
    private final UserService userService;
    private final UserQueryService userQueryService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<UserDto>> getUsers(UserCriteria criteria, @PageableDefault Pageable pageable) {
        final Page<UserDto> page = userQueryService.getUsers(criteria, pageable);
        final HttpHeaders headers = ResponseUtils.generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<UserDto>> getUsers(UserCriteria criteria) {
        return ResponseEntity.ok(userQueryService.getUsers(criteria));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') ")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseUtils.wrapOrNotFound(userQueryService.getUser(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }
}
