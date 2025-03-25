package vn.sparkminds.template.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {
    private Long id;
    private String address;
    private BigDecimal balance;
    private CoinDto coin;
    private UserDto user;
    private NetworkDto network;
    private BigDecimal balancePrice;
}
