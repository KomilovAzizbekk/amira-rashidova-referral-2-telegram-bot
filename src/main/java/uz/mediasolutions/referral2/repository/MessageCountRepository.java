package uz.mediasolutions.referral2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.referral2.entity.MessageCount;

public interface MessageCountRepository extends JpaRepository<MessageCount, Long> {

    MessageCount findByMessageId(Integer messageId);

}
