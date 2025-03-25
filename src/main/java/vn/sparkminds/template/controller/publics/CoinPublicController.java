package vn.sparkminds.template.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.sparkminds.template.service.CoinQueryService;
import vn.sparkminds.template.service.criteria.CoinCriteria;
import vn.sparkminds.template.service.dto.CoinDto;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/coins")
@RequiredArgsConstructor
public class CoinPublicController {

    private final CoinQueryService coinQueryService;

    @GetMapping
    public ResponseEntity<List<CoinDto>> getCoins(CoinCriteria criteria) {
        return ResponseEntity.ok(coinQueryService.getCoins(criteria));
    }

}
