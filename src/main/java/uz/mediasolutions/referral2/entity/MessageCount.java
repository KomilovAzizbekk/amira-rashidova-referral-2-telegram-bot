package uz.mediasolutions.referral2.entity;

import javax.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "message_count")
public class MessageCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer messageId;

    private int heartCount;

    private int fireCount;

    private int likeCount;

    private int eyesCount;

    private int hundredCount;

    @Column(name = "double_click")
    private boolean doubleClick = false;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Reaction> reactions;


    public MessageCount(Integer messageId, int heart, int fire, int like, int eyes, int hundred) {
        this.messageId = messageId;
        this.heartCount = heart;
        this.fireCount = fire;
        this.likeCount = like;
        this.eyesCount = eyes;
        this.hundredCount = hundred;
    }
}
