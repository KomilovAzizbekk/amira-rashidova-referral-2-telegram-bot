package uz.mediasolutions.referral2.service.abs;

import org.springframework.data.domain.Page;
import uz.mediasolutions.referral2.manual.ApiResult;
import uz.mediasolutions.referral2.payload.TgUserDTO;

public interface UserService {

    ApiResult<Page<TgUserDTO>> getAll(int page, int size);
}
