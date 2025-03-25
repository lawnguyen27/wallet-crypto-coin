package vn.sparkminds.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.sparkminds.template.service.criteria.UserCriteria;
import vn.sparkminds.template.service.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    Page<UserDto> getUsers(UserCriteria criteria, Pageable pageable);

    List<UserDto> getUsers(UserCriteria criteria);

    Optional<UserDto> getUser(Long id);

    Optional<UserDto> getUserByEmail(String email);

}

