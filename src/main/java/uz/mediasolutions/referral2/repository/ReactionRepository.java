package uz.mediasolutions.referral2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.referral2.entity.MessageCount;
import uz.mediasolutions.referral2.entity.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Reaction findByMessageId(Integer messageId);

    boolean existsByMessageIdAndUserChatId(Integer messageId, String chatId);

    boolean existsByMessageIdAndChatId(Integer messageId, String chatId);

}
