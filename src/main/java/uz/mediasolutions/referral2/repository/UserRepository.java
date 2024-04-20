package uz.mediasolutions.referral2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.referral2.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByIdAndEnabledIsTrueAndAccountNonExpiredIsTrueAndCredentialsNonExpiredIsTrueAndAccountNonLockedIsTrue(UUID userId);

    Optional<User> findFirstByUsernameAndEnabledIsTrueAndAccountNonExpiredIsTrueAndAccountNonLockedIsTrueAndCredentialsNonExpiredIsTrue(String username);

    boolean existsByUsername(String username);

}
