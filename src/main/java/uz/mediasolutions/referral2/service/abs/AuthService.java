package uz.mediasolutions.referral2.service.abs;

import uz.mediasolutions.referral2.entity.User;
import uz.mediasolutions.referral2.manual.ApiResult;
import uz.mediasolutions.referral2.payload.SignInDTO;
import uz.mediasolutions.referral2.payload.TokenDTO;

public interface AuthService {

    ApiResult<TokenDTO> signIn(SignInDTO signInDTO);

    TokenDTO generateToken(User user);

    User checkUsernameAndPasswordAndEtcAndSetAuthenticationOrThrow(String username, String password);


}
