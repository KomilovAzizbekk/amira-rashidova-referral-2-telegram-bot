package uz.mediasolutions.referral2.mapper;

import org.mapstruct.Mapper;
import uz.mediasolutions.referral2.entity.TgUser;
import uz.mediasolutions.referral2.payload.TgUserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    TgUserDTO toDTO(TgUser tgUser);

}
