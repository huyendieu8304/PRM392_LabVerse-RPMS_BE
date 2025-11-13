package com.prm392.be.labverse.service;

import com.prm392.be.labverse.constant.ERole;
import com.prm392.be.labverse.dto.user.UpdateUserRequest;
import com.prm392.be.labverse.dto.user.UserDto;
import com.prm392.be.labverse.dto.user.UserSimpleResponse;
import com.prm392.be.labverse.dto.user.RegisterAccountRequest;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.entity.Role;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.repository.UserRepository;
import com.prm392.be.labverse.repository.RoleRepository;
import com.prm392.be.labverse.security.CurrentUserInfo;
import com.prm392.be.labverse.security.CurrentUserProvider;
import com.prm392.be.labverse.security.UserDetailsImpl;
import com.prm392.be.labverse.util.OtpUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final MailService mailService;

    private final OtpUtil otpUtil;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;


    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           MailService mailService,
                           OtpUtil otpUtil,
                           PasswordEncoder passwordEncoder, CurrentUserProvider currentUserProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mailService = mailService;
        this.otpUtil = otpUtil;
        this.passwordEncoder = passwordEncoder;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public UserSimpleResponse createUser(RegisterAccountRequest request) {

        String email = request.getEmail().trim();

        Optional<User> optionalUser = userRepository.findByEmail(email);

        User user;

        if (optionalUser.isEmpty()){
            //chua co tai khoan, tao moi
            log.info("Create new account: {}", email);
            user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(user);
        } else {
            //tk với email đã tồn taij
            log.info("Account existed no create or sent otp");
            user = optionalUser.get();
            if (Boolean.FALSE.equals(user.getDeleteFlag())) {
                //email da duoc su dung
                throw new AppException(UserErrorCode.EMAIL_USED);
            }
        }
        //tk inactive, gửi lại mail cho hoj luon

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
        if (!user.getDeleteFlag()){
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
        if (!user.getDeleteFlag()){
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

    @Override
    public UserSimpleResponse selectRole(String userId, String roleName) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND)
        );

        //khong cho set lai role
        if (user.getRole() != null){
            throw new AppException(UserErrorCode.USER_ROLE_ALREADY_SET);
        }

        Role role = roleRepository.findByName(ERole.valueOf(roleName.trim().toUpperCase()))
                .orElseThrow(() -> new AppException(UserErrorCode.ROLE_NOT_EXIST_IN_DB));
        user.setRole(role);
        userRepository.save(user);

        return new UserSimpleResponse(user.getEmail(), user.getRole().getName().name());
    }

    // ----------------- GET PROFILE -----------------
    @Transactional(readOnly = true)
    public UserDto getMe() {
        CurrentUserInfo me = requireCurrent();
        User user = findByIdOrEmail(me.getUserId(), me.getEmail())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        return toDto(user);
    }

    @Transactional
    public UserDto updateMe(UpdateUserRequest req) {
        CurrentUserInfo me = requireCurrent();
        User u = findByIdOrEmail(me.getUserId(), me.getEmail())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (req.full_name != null)    u.setFullName(req.full_name.trim());
        if (req.phone_number != null) u.setPhoneNumber(req.phone_number.trim());
        if (req.address != null)      u.setAddress(req.address.trim());

        // gender: tuỳ schema của bạn (Boolean/Enum/String)
        if (req.gender != null)       u.setGender(req.gender);

        u.setUpdatedAt(LocalDateTime.now());
        userRepository.save(u);
        return toDto(u);
    }

    // ---------- helpers ----------
    private CurrentUserInfo requireCurrent() {
        CurrentUserInfo me = currentUserProvider.get();
        if (me == null) throw new RuntimeException("UNAUTHENTICATED");
        if ((me.getUserId() == null || me.getUserId().isBlank())
                && (me.getEmail() == null || me.getEmail().isBlank())) {
            throw new RuntimeException("PRINCIPAL_INVALID");
        }
        return me;
    }

    private Optional<User> findByIdOrEmail(String id, String email) {
        if (id != null && !id.isBlank()) {
            Optional<User> byId = userRepository.findById(id);
            if (byId.isPresent()) return byId;
        }
        if (email != null && !email.isBlank()) {
            return userRepository.findByEmail(email.trim());
            // hoặc findByEmailIgnoreCase(email.trim())
        }
        return Optional.empty();
    }


    private static UserDto toDto(User u) {
        UserDto d = new UserDto();
        d.id = u.getId();
        d.full_name = u.getFullName();
        d.email = u.getEmail();
        d.phone_number = u.getPhoneNumber();
        d.gender = u.isGender();
        d.address = u.getAddress();
        d.role_id = u.getRole().getId();
        d.delete_flag = u.getDeleteFlag();
        d.created_at = u.getCreatedAt();
        d.updated_at = u.getUpdatedAt();
        return d;
    }
}
