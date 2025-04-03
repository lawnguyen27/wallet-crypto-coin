package vn.sparkminds.template.service.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.service.dto.WalletDto;

@Service
@AllArgsConstructor
public class WalletMapper {

    private final CoinMapper coinMapper;
    private final UserMapper userMapper;
    private final NetworkMapper networkMapper;

    public WalletDto toDto(Wallet entity) {
        return WalletDto.builder()
                .id(entity.getId())
                .balance(entity.getBalance())
                .user(userMapper.toDto(entity.getUser()))
                .coin(coinMapper.toDto(entity.getCoin()))

                .address(entity.getAddress())
                .network(networkMapper.toDto(entity.getNetwork()))
                .balancePrice(entity.getBalance().multiply(entity.getCoin().getPrice()))
                .pendingBalance(entity.getPendingBalance())
                .build();
    }

    public Wallet toEntity(WalletDto dto, Coin coin, User user) {
        return Wallet.builder()
                .id(dto.getId())
                .balance(dto.getBalance())
                .coin(coin)
                .user(user)
                .build();
    }
}


