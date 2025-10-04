package sit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.backend.entities.UserAccount;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByNickname(String nickname);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByFullname(String fullname);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<UserAccount> findByVerificationToken(String token);
}
