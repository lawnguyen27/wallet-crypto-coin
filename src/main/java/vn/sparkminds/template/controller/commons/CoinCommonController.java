package vn.sparkminds.template.controller.commons;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.sparkminds.template.service.CoinQueryService;
import vn.sparkminds.template.service.criteria.CoinCriteria;
import vn.sparkminds.template.service.dto.CoinDto;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/common/v1/coins")
@RequiredArgsConstructor
public class CoinCommonController {

    private final CoinQueryService coinQueryService;

    @GetMapping
    public ResponseEntity<List<CoinDto>> getCoins(CoinCriteria criteria, @PageableDefault Pageable pageable) {
        final Page<CoinDto> page = coinQueryService.getCoinsEnable(criteria, pageable);
        final HttpHeaders headers = ResponseUtils.generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


}
