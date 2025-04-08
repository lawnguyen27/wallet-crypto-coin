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
import vn.sparkminds.template.service.LimitTokenQueryService;
import vn.sparkminds.template.service.LimitTokenService;
import vn.sparkminds.template.service.criteria.LimitTokenCriteria;
import vn.sparkminds.template.service.dto.LimitTokenDto;
import vn.sparkminds.template.service.dto.request.LimitTokenUpdateRequestDto;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/private/v1/limit-tokens")
@RequiredArgsConstructor
public class LimitTokenPrivateController {

    private final LimitTokenQueryService limitTokenQueryService;
    private final LimitTokenService limitTokenService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAnyAuthority('viewLimitTokens')")
    public ResponseEntity<List<LimitTokenDto>> getLimitTokens(LimitTokenCriteria criteria, @PageableDefault Pageable pageable) {
        final Page<LimitTokenDto> page = limitTokenQueryService.getLimitTokens(criteria, pageable);
        final HttpHeaders headers = ResponseUtils.generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAnyAuthority('viewLimitTokens')")
    public ResponseEntity<List<LimitTokenDto>> getLimitTokens(LimitTokenCriteria criteria) {
        return ResponseEntity.ok(limitTokenQueryService.getLimitTokens(criteria));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAnyAuthority('viewLimitTokens')")
    public ResponseEntity<LimitTokenDto> getLimitToken(@PathVariable Long id) {
        return ResponseUtils.wrapOrNotFound(limitTokenQueryService.getLimitToken(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAuthority('editLimitTokens')")
    public ResponseEntity<Void> updateLimitToken(@PathVariable Long id, @Valid @RequestBody LimitTokenUpdateRequestDto request) {
        limitTokenService.updateLimitToken(id, request);
        return ResponseEntity.noContent().build();
    }

}
