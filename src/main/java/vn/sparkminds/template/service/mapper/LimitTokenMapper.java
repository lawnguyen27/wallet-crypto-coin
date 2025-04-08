package vn.sparkminds.template.service.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.sparkminds.template.entity.LimitToken;
import vn.sparkminds.template.service.dto.LimitTokenDto;

@Service
@AllArgsConstructor
public class LimitTokenMapper {

    private final CoinMapper coinMapper;

    public LimitTokenDto toDto(LimitToken entity) {
        return LimitTokenDto.builder()
                .id(entity.getId())
                .limitQuantity(entity.getLimitQuantity())
                .coin(coinMapper.toDto(entity.getCoin()))
                .build();
    }

    public LimitToken toEntity(LimitTokenDto dto) {
        LimitToken limitToken = new LimitToken();
        limitToken.setId(dto.getId());
        limitToken.setLimitQuantity(dto.getLimitQuantity());
        return limitToken;
    }
}
