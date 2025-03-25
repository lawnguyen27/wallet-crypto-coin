package vn.sparkminds.template.service.mapper;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.Mapping;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.enumeration.UserRole;
import vn.sparkminds.template.service.dto.UserDto;
@Service
public class UserMapper {

    public UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .role(entity.getRole())
                .vaultId(entity.getVaultId())
                .build();
    }

    public User toEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .role(dto.getRole())
                .vaultId(dto.getVaultId())
                .build();
    }
}

