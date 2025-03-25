package vn.sparkminds.template.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.sparkminds.template.service.UserQueryService;
import vn.sparkminds.template.service.criteria.UserCriteria;
import vn.sparkminds.template.service.dto.UserDto;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/users")
@RequiredArgsConstructor
public class UserPublicController {

    private final UserQueryService userQueryService;

    @GetMapping("")
    public ResponseEntity<UserDto> getUser( Authentication authentication) {
        String email = authentication.getName();
        return ResponseUtils.wrapOrNotFound(userQueryService.getUserByEmail(email));
    }
    @GetMapping("list")
    public ResponseEntity<List<UserDto>> getUsers(UserCriteria criteria, @PageableDefault Pageable pageable) {
        final Page<UserDto> page = userQueryService.getUsers(criteria, pageable);
        final HttpHeaders headers = ResponseUtils.generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
