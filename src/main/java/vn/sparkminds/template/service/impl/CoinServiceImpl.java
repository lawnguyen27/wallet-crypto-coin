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

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=ethereum,solana&vs_currencies=usd";


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

    @Scheduled(fixedRate = 300000)
    @Transactional
    @Override
    public void updateTestCoinPrices() {
        log.info("Start update coin");

        Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);

        if (response == null) {
            log.error("Fail to get API from CoinGecko");
            return;
        }

        BigDecimal ethPrice = getPriceFromResponse(response, "ethereum");
        BigDecimal solPrice = getPriceFromResponse(response, "solana");

        if (ethPrice != null) {
            coinRepository.findCoinBySymbolAndIsEnabledIsTrue("ETH_TEST6").ifPresent(coin -> {
                coin.setPrice(ethPrice);
                coinRepository.save(coin);
                log.info("Update ETH_TEST6: ${}", ethPrice);
            });
        }

        if (solPrice != null) {
            coinRepository.findCoinBySymbolAndIsEnabledIsTrue("SOL_TEST").ifPresent(coin -> {
                coin.setPrice(solPrice);
                coinRepository.save(coin);
                log.info("Update SOL_TEST: ${}", solPrice);
            });
        }
    }


    private BigDecimal getPriceFromResponse(Map<String, Object> response, String coinName) {
        Map<String, Object> coinData = (Map<String, Object>) response.getOrDefault(coinName, Map.of());
        Object priceObj = coinData.get("usd");

        if (priceObj instanceof Number) {
            return BigDecimal.valueOf(((Number) priceObj).doubleValue()); // Chuyển đổi an toàn
        }
        return null;
    }

}
