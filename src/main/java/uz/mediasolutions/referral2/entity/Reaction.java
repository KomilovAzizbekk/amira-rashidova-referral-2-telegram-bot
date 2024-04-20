package uz.mediasolutions.referral2.entity;

import javax.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "reactions")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "message_id")
    private Integer messageId;

    @ManyToOne
    private TgUser user;

    public Reaction(String heart, Integer messageId, TgUser user, String chatId) {
        this.name = heart;
        this.messageId = messageId;
        this.user = user;
        this.chatId = chatId;
    }

    public Reaction(String heart, Integer messageId, String chatId) {
        this.name = heart;
        this.messageId = messageId;
        this.chatId = chatId;
    }


}
