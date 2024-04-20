package uz.mediasolutions.referral2.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import uz.mediasolutions.referral2.controller.abs.UserController;
import uz.mediasolutions.referral2.manual.ApiResult;
import uz.mediasolutions.referral2.payload.TgUserDTO;
import uz.mediasolutions.referral2.service.abs.UserService;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ApiResult<Page<TgUserDTO>> getAllUsers(int page, int size) {
        return userService.getAll(page, size);
    }
}
