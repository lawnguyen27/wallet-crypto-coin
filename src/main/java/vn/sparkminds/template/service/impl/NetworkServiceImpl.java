package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.Network;
import vn.sparkminds.template.repository.NetworkRepository;
import vn.sparkminds.template.service.NetworkService;

@Service
@RequiredArgsConstructor
public class NetworkServiceImpl implements NetworkService {
    private final NetworkRepository networkRepository;

    @Override
    public Network resolveNetworkByCoin(Coin coin) {
        if ("SOL_TEST".equalsIgnoreCase(coin.getSymbol())) {
            return networkRepository.findByNetworkType("SOLANA");
        } else {
            return networkRepository.findByNetworkType("ETHEREUM");
        }
    }
}
