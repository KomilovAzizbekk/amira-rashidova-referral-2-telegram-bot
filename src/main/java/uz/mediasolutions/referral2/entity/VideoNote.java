package uz.mediasolutions.referral2.entity;

import javax.persistence.*;
import lombok.*;
import uz.mediasolutions.referral2.enums.StepName;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "video_note")
public class VideoNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id")
    private String fileId;

}
