package vn.sparkminds.template.controller.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.sparkminds.template.service.WalletQueryService;
import vn.sparkminds.template.service.criteria.WalletCriteria;
import vn.sparkminds.template.service.dto.WalletDto;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/private/v1/wallets")
@RequiredArgsConstructor
public class WalletPrivateController {
    private final WalletQueryService walletQueryService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<WalletDto>> getWallets(WalletCriteria criteria, @PageableDefault Pageable pageable) {
        final Page<WalletDto> page = walletQueryService.getWallets(criteria, pageable);
        final HttpHeaders headers = ResponseUtils.generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<WalletDto>> getWallets(WalletCriteria criteria) {
        return ResponseEntity.ok(walletQueryService.getWallets(criteria));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<WalletDto> getWallet(@PathVariable Long id) {
        return ResponseUtils.wrapOrNotFound(walletQueryService.getWallet(id));
    }
}