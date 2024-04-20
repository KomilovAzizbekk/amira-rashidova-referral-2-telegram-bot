package uz.mediasolutions.referral2.controller.abs;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.mediasolutions.referral2.manual.ApiResult;
import uz.mediasolutions.referral2.payload.TgUserDTO;
import uz.mediasolutions.referral2.utills.constants.Rest;

@RequestMapping(UserController.USERS)
public interface UserController {

    String USERS = Rest.BASE_PATH + "users/";
    String GET_ALL_USERS = "get-all";

    @GetMapping(GET_ALL_USERS)
    ApiResult<Page<TgUserDTO>> getAllUsers(@RequestParam(defaultValue = Rest.DEFAULT_PAGE_NUMBER) int page,
                                           @RequestParam(defaultValue = Rest.DEFAULT_PAGE_SIZE) int size);

}
