package vn.sparkminds.template.controller.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.sparkminds.template.service.NetworkQueryService;
import vn.sparkminds.template.service.dto.NetworkDto;
import vn.sparkminds.template.service.criteria.NetworkCriteria;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/private/v1/networks")
@RequiredArgsConstructor
public class NetworkPrivateController {
    private final NetworkQueryService networkQueryService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<NetworkDto>> getNetworks(NetworkCriteria criteria, @PageableDefault Pageable pageable) {
        final Page<NetworkDto> page = networkQueryService.getNetworks(criteria, pageable);
        final HttpHeaders headers = ResponseUtils.generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<NetworkDto>> getNetworks(NetworkCriteria criteria) {
        return ResponseEntity.ok(networkQueryService.getNetworks(criteria));
    }

}
