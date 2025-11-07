package com.prm392.be.labverse.service;

import com.prm392.be.labverse.constant.ERole;
import com.prm392.be.labverse.dto.user.UserSimpleResponse;
import com.prm392.be.labverse.dto.user.RegisterAccountRequest;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.entity.Role;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.repository.UserRepository;
import com.prm392.be.labverse.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    UserRepository userRepository;
    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;


    @Override
    public UserSimpleResponse createUser(RegisterAccountRequest request) {
        //kiem tra emial da duoc dung chuwa
        if ( userRepository.findByEmail(request.getEmail().trim()).isPresent()) {
            throw new AppException(UserErrorCode.EMAIL_USED);
        }

        log.info("Create new account: {}", request);

        Role role = roleRepository.findByName(ERole.valueOf(request.getRoleName().trim().toUpperCase()))
                .orElseThrow(() -> new AppException(UserErrorCode.ROLE_NOT_EXIST_IN_DB));

        User user = User.builder()
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role).build();
        userRepository.save(user);
        return new UserSimpleResponse(request.getEmail(), role.getName().name());
    }

    @Override
    public User findOrCreateUser(String email, String name, String password) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setFullName(name);
                    user.setPassword(password);
                    //thôi, kệ để người dùng tự sửa avatar sau
//                    user.setAvatar(pictureUrl);
                    return userRepository.save(user);
                });
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND)
        );
    }
}
