package uz.mediasolutions.referral2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.referral2.entity.Step;
import uz.mediasolutions.referral2.entity.VideoNote;
import uz.mediasolutions.referral2.enums.StepName;

public interface VideoNoteRepository extends JpaRepository<VideoNote, Long> {



}
