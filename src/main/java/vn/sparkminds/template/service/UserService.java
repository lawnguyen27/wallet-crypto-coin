package vn.sparkminds.template.service;

import com.fireblocks.sdk.ApiException;
import vn.sparkminds.template.entity.Network;
import vn.sparkminds.template.entity.User;
import vn.sparkminds.template.service.dto.request.RegisterRequestDto;

import java.util.concurrent.ExecutionException;

public interface UserService {
    RegisterRequestDto registerUser(RegisterRequestDto request);
    void blockUser(Long id);
}
