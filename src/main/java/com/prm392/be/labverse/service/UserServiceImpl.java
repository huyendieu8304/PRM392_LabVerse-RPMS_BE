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
import com.prm392.be.labverse.util.OtpUtil;
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

    MailService mailService;

    OtpUtil otpUtil;
    PasswordEncoder passwordEncoder;


    @Override
    public UserSimpleResponse createUser(RegisterAccountRequest request) {
        //kiem tra emial da duoc dung chuwa
        if ( userRepository.findByEmail(request.getEmail().trim()).isPresent()) {
            throw new AppException(UserErrorCode.EMAIL_USED);
        }

        log.info("Create new account: {}", request);

//        Role role = roleRepository.findByName(ERole.valueOf(request.getRoleName().trim().toUpperCase()))
//                .orElseThrow(() -> new AppException(UserErrorCode.ROLE_NOT_EXIST_IN_DB));

        String email = request.getEmail().trim();
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        //gửi mail verify account
        String otp = otpUtil.generateVerifyAccOtp(email);

        mailService.sendRegisterOTP(email, otp);
        return new UserSimpleResponse(email, null);
    }

    @Override
    public User findOrCreateUser(String email, String name, String password) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setFullName(name);
                    user.setPassword(password);
                    //active account luôn, ko can gui mail nua
                    user.setDeleteFlag(false);
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

    @Override
    public void resentOtpVerifyAccount(String email) {
        //kiểm tra tài khoản đang trong trạng thái nào
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND)
        );
        // Nếu tài khoản đã kích hoạt rồi
        if (!user.isDeleteFlag()){
            throw new AppException(UserErrorCode.ACCOUNT_VERIFIED);
        }
        //tạo otp mới
        String otp = otpUtil.regenerateVerifyAccOtp(email);
        //gửi lại mail
        mailService.sendRegisterOTP(email, otp);
    }

    @Override
    public void verifyAccount(String email, String otp) {
        //kiểm tra tài khoản đang trong trạng thái nào
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND)
        );
        // Nếu tài khoản đã kích hoạt rồi
        if (!user.isDeleteFlag()){
            throw new AppException(UserErrorCode.ACCOUNT_VERIFIED);
        }

        // Nếu OTP sai hoặc hết hạn
        if (!otpUtil.isValidVerifyAccOtp(email, otp)){
            throw new AppException(UserErrorCode.INVALID_VERIFIED_OTP);
        }

        // OTP hợp lệ -> kích hoạt tài khoản
        user.setDeleteFlag(false);
        userRepository.save(user);
    }
}
