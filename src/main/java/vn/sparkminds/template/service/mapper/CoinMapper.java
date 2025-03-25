package vn.sparkminds.template.service.mapper;

import org.springframework.stereotype.Service;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.service.dto.CoinDto;

@Service
public class CoinMapper {
    public CoinDto toDto(Coin entity) {
        return CoinDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .symbol(entity.getSymbol())
                .logoUrl(entity.getLogoUrl())
                .balance(entity.getPrice())
                .build();
    }
}
