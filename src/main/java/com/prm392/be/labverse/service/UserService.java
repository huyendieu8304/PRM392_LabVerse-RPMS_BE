package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.user.UserSimpleResponse;
import com.prm392.be.labverse.dto.user.RegisterAccountRequest;
import com.prm392.be.labverse.entity.User;

public interface UserService {

    UserSimpleResponse createUser(RegisterAccountRequest request);

    User findOrCreateUser(String email, String name, String roleName, String password);

    User findUserByEmail(String email);
}
