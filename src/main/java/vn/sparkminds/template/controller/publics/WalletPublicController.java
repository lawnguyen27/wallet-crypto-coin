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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.repository.WalletRepository;
import vn.sparkminds.template.service.NetworkQueryService;
import vn.sparkminds.template.service.WalletQueryService;
import vn.sparkminds.template.service.criteria.NetworkCriteria;
import vn.sparkminds.template.service.criteria.WalletCriteria;
import vn.sparkminds.template.service.dto.NetworkDto;
import vn.sparkminds.template.service.dto.WalletDto;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/wallets")
@RequiredArgsConstructor
public class WalletPublicController {

    private final WalletQueryService walletQueryService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<WalletDto>> getWallets(WalletCriteria criteria, @PageableDefault Pageable pageable, Authentication authentication) {

        final Page<WalletDto> page = walletQueryService.getWallets(criteria, pageable, authentication.getName());

        final HttpHeaders headers = ResponseUtils
                .generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
