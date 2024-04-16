package uz.mediasolutions.referral2.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.mediasolutions.referral2.enums.StepName;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "step")
public class VideoNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id")
    private String fileId;

}
