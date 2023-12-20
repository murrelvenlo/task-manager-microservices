package fact.it.userservice.repository;

import fact.it.userservice.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findFirstByEmail(String email);
    UserEntity findByUserCode(String taskCode);
    void deleteByUserCode(String userCode);
    boolean existsByEmail(String email);
}
