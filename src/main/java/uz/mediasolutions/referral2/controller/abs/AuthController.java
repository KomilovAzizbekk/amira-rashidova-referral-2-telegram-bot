package uz.mediasolutions.referral2.controller.abs;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.mediasolutions.referral2.manual.ApiResult;
import uz.mediasolutions.referral2.payload.SignInDTO;
import uz.mediasolutions.referral2.payload.TokenDTO;
import uz.mediasolutions.referral2.utills.constants.Rest;

import javax.validation.Valid;

@RequestMapping(AuthController.AUTH)
public interface AuthController {

    String AUTH = Rest.BASE_PATH + "auth/";

    String SIGN_IN = "sign-in";

    @PostMapping(SIGN_IN)
    ApiResult<TokenDTO> signIn(@RequestBody @Valid SignInDTO dto);


}
