package uz.mediasolutions.referral2.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.mediasolutions.referral2.entity.TgUser;
import uz.mediasolutions.referral2.manual.ApiResult;
import uz.mediasolutions.referral2.mapper.UserMapper;
import uz.mediasolutions.referral2.payload.TgUserDTO;
import uz.mediasolutions.referral2.repository.TgUserRepository;
import uz.mediasolutions.referral2.service.abs.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TgUserRepository tgUserRepository;
    private final UserMapper userMapper;

    @Override
    public ApiResult<Page<TgUserDTO>> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TgUser> all = tgUserRepository.findAll(pageable);
        Page<TgUserDTO> map = all.map(userMapper::toDTO);
        return ApiResult.success(map);
    }
}
