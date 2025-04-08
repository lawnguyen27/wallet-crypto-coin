package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import vn.sparkminds.template.config.error.http.NotFoundException;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.repository.CoinRepository;
import vn.sparkminds.template.service.CoinService;
import vn.sparkminds.template.service.dto.request.UpdateCoinRequestDto;



@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;

    @Override
    @Transactional
    public void updateCoin(Long coinId, UpdateCoinRequestDto request) {
        log.info("Update coin request: {}", request);

        final Coin coin = coinRepository.findByIdAndIsEnabledTrue(coinId).orElseThrow(NotFoundException::new);
        coin.setName(request.getName());
        coin.setLogoUrl(request.getLogoUrl());

        coinRepository.save(coin);
        log.info("Coin updated successfully: {}", coin);
    }

    @Override
    @Transactional
    public void deleteCoin(Long coinId) {
        log.info("Delete coin request: {}", coinId);

        final Coin coin = coinRepository.findByIdAndIsEnabledTrue(coinId).orElseThrow(NotFoundException::new);
        coin.setIsEnabled(false);

        coinRepository.save(coin);
        log.info("Coin deleted successfully: {}", coin);
    }
}
