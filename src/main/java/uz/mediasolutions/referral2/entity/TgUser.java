package uz.mediasolutions.referral2.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import uz.mediasolutions.referral2.entity.template.AbsLong;

import javax.persistence.*;

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

    @Column(name = "name")
    private String name;

    @Column(name = "comments")
    private int comments;

    @Column(name = "reactions")
    private int reactions;

    @Column(name = "invited_people")
    private int invitedPeople;

    @Column(name = "points")
    private int points;

    @Column(name = "invite_link")
    private String inviteLink;

    @ManyToOne(fetch = FetchType.LAZY)
    private Step step;
}
