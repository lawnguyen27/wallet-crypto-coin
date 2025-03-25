package vn.sparkminds.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.entity.Wallet;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findUserByEmailAndIsEnabledIsTrue(String email);
    User findByEmail(String email);
    Optional<User> findByEmailAndIsEnabledIsTrue(String email);
    Optional<User> findUserByVaultId(String id);
}
