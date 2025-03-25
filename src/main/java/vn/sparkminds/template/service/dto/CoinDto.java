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
public class CoinDto {
    private Long id;
    private String name;
    private String symbol;
    private String logoUrl;
    private BigDecimal balance;
}
