package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.user.UpdateUserRequest;
import com.prm392.be.labverse.dto.user.UserDto;
import com.prm392.be.labverse.dto.user.UserSimpleResponse;
import com.prm392.be.labverse.dto.user.RegisterAccountRequest;
import com.prm392.be.labverse.entity.User;

public interface UserService {

    UserSimpleResponse createUser(RegisterAccountRequest request);

    User findOrCreateUser(String email, String name, String password);

    User findUserByEmail(String email);

    void resentOtpVerifyAccount(String email);

    void verifyAccount(String email, String otp);

    UserSimpleResponse selectRole(String userId, String roleName);
    UserDto getMe();
    UserDto updateMe(UpdateUserRequest req);

}
