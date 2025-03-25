package vn.sparkminds.template.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sparkminds.template.config.jpa.specifications.QueryService;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.User_;
import vn.sparkminds.template.repository.UserRepository;
import vn.sparkminds.template.service.UserQueryService;
import vn.sparkminds.template.service.criteria.UserCriteria;
import vn.sparkminds.template.service.dto.UserDto;
import vn.sparkminds.template.service.mapper.UserMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl extends QueryService<User> implements UserQueryService {

    /**
     * Repositories
     **/
    private final UserRepository userRepository;

    /**
     * Mappers
     **/
    private final UserMapper userMapper;

    @Override
    public Page<UserDto> getUsers(UserCriteria criteria, Pageable pageable) {
        final Specification<User> specification = buildSpecification(criteria);
        return userRepository.findAll(specification, pageable).map(userMapper::toDto);
    }

    @Override
    public List<UserDto> getUsers(UserCriteria criteria) {
        final Specification<User> specification = buildSpecification(criteria);
        return userRepository.findAll(specification).stream().map(userMapper::toDto).toList();
    }

    @Override
    public Optional<UserDto> getUser(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmailAndIsEnabledIsTrue(email).map(userMapper::toDto);
    }

    private Specification<User> buildSpecification(UserCriteria criteria) {
        Specification<User> specification = Specification.where(null);

        if (Objects.isNull(criteria)) {
            return specification;
        }

        if (Objects.nonNull(criteria.getId())) {
            specification = specification.and(buildRangeSpecification(criteria.getId(), User_.id));
        }

        if (Objects.nonNull(criteria.getName())) {
            specification = specification.and(buildStringSpecification(criteria.getName(), User_.name));
        }

        if (Objects.nonNull(criteria.getEmail())) {
            specification = specification.and(buildStringSpecification(criteria.getEmail(), User_.email));
        }

        if (Objects.nonNull(criteria.getRole())) {
            specification = specification.and(buildSpecification(criteria.getRole(), User_.role));
        }


        if (Objects.nonNull(criteria.getVaultId())) {
            specification = specification.and(buildStringSpecification(criteria.getVaultId(), User_.vaultId));
        }

        return specification;
    }
}
