package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.error.http.NotFoundException;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.LimitToken;
import vn.sparkminds.template.repository.CoinRepository;
import vn.sparkminds.template.repository.LimitTokenRepository;
import vn.sparkminds.template.service.LimitTokenService;
import vn.sparkminds.template.service.dto.LimitTokenDto;
import vn.sparkminds.template.service.dto.request.LimitTokenUpdateRequestDto;
import vn.sparkminds.template.service.dto.request.UpdateCoinRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LimitTokenServiceImpl  implements LimitTokenService {

    private final LimitTokenRepository limitTokenRepository;
    private final CoinRepository coinRepository;

    @Override
    @Transactional
    public void updateLimitToken(Long coinId, LimitTokenUpdateRequestDto request){
        log.info("Update limit token request: {}", request);

        final Coin coin = coinRepository.findByIdAndIsEnabledTrue(coinId).orElseThrow(NotFoundException::new);
        LimitToken limit= limitTokenRepository.findByCoinId(coinId).orElseThrow(NotFoundException::new);
        limit.setLimitQuantity(request.getLimit());
        limitTokenRepository.save(limit);
        log.info("Limit Token updated for {} successfully with quantity: {}", coin.getSymbol(), request.getLimit());
    }
}

