package uz.mediasolutions.referral2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.referral2.entity.TgUser;

public interface TgUserRepository extends JpaRepository<TgUser, Long> {

    TgUser findByChatId(String chatId);

    boolean existsByChatId(String chatId);
}
