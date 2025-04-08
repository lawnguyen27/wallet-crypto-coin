package vn.sparkminds.template.service;

import vn.sparkminds.template.service.dto.request.UpdateCoinRequestDto;

public interface CoinService {

    void updateCoin(Long coinId, UpdateCoinRequestDto request);

    void deleteCoin(Long coinId);

}
