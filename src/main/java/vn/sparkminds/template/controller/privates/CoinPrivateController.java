package vn.sparkminds.template.controller.privates;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.sparkminds.template.service.CoinQueryService;
import vn.sparkminds.template.service.CoinService;
import vn.sparkminds.template.service.criteria.CoinCriteria;
import vn.sparkminds.template.service.dto.CoinDto;
import vn.sparkminds.template.service.dto.request.UpdateCoinRequestDto;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/private/v1/coins")
@RequiredArgsConstructor
public class CoinPrivateController {

    private final CoinService coinService;
    private final CoinQueryService coinQueryService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAnyAuthority('viewCoins')")
    public ResponseEntity<List<CoinDto>> getCoins(CoinCriteria criteria, @PageableDefault Pageable pageable) {
        final Page<CoinDto> page = coinQueryService.getCoins(criteria, pageable);
        final HttpHeaders headers = ResponseUtils.generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAnyAuthority('viewCoins')")
    public ResponseEntity<List<CoinDto>> getCoins(CoinCriteria criteria) {
        return ResponseEntity.ok(coinQueryService.getCoins(criteria));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAnyAuthority('viewCoins')")
    public ResponseEntity<CoinDto> getCoin(@PathVariable Long id) {
        return ResponseUtils.wrapOrNotFound(coinQueryService.getCoin(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAuthority('editCoins')")
    public ResponseEntity<Void> updateCoin(@PathVariable Long id, @Valid @RequestBody UpdateCoinRequestDto request) {
        coinService.updateCoin(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAuthority('deleteCoins')")
    public ResponseEntity<Void> deleteCoin(@PathVariable Long id) {
        coinService.deleteCoin(id);
        return ResponseEntity.noContent().build();
    }
}
