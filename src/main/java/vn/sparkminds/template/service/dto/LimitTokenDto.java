package vn.sparkminds.template.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitTokenDto {
    private Long id;
    private BigDecimal limitQuantity;
    private CoinDto coin;
}
