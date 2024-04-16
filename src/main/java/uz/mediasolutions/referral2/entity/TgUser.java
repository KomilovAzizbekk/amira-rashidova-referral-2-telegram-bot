package uz.mediasolutions.referral2.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import uz.mediasolutions.referral2.entity.template.AbsLong;

import jakarta.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "tg_users")
public class TgUser extends AbsLong {

    @Column(name = "chat_id")
    private String chatId;

//    @Column(name = "name")
//    private String name;
//
//    @Column(name = "phone_number")
//    private String phoneNumber;

    @Column(name = "invite_link")
    private String inviteLink;

    @ManyToOne(fetch = FetchType.LAZY)
    private Step step;
}
