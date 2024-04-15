package uz.mediasolutions.referral2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.referral2.entity.Step;
import uz.mediasolutions.referral2.entity.TgUser;

public interface StepRepository extends JpaRepository<Step, Long> {
}
