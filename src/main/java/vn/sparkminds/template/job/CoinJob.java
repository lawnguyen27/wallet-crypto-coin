package vn.sparkminds.template.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import vn.sparkminds.template.repository.CoinRepository;
import vn.sparkminds.template.service.CoinService;

import java.math.BigDecimal;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class CoinJob {

    private final CoinRepository coinRepository;
    private final RestTemplate restTemplate;
    private static final String API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=ethereum,solana&vs_currencies=usd";


    @Scheduled(fixedRate = 300000)
    @Transactional
    public void updateTestCoinPrices() {

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
