package uz.mediasolutions.referral2.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TgUserDTO {

    private String chatId;

    private String name;

    private long comments;

    private long reactions;

    private long points;

}
