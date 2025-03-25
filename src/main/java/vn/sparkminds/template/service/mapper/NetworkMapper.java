package vn.sparkminds.template.service.mapper;

import org.springframework.stereotype.Service;
import vn.sparkminds.template.entity.Network;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.service.dto.NetworkDto;
@Service
public class NetworkMapper {
    public NetworkDto toDto(Network entity) {
        return NetworkDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .networkType(entity.getNetworkType())
                .build();
    }

    public Network toEntity(NetworkDto dto) {
        return Network.builder()
                .id(dto.getId())
                .name(dto.getName())
                .networkType(dto.getNetworkType())
                .build();
    }
}


