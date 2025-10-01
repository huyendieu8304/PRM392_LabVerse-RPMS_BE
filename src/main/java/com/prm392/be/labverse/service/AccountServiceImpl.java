package com.prm392.be.labverse.service;

import com.prm392.be.labverse.constant.ERole;
import com.prm392.be.labverse.dto.account.AccountSimpleResponse;
import com.prm392.be.labverse.dto.account.RegisterAccountRequest;
import com.prm392.be.labverse.entity.Account;
import com.prm392.be.labverse.entity.Role;
import com.prm392.be.labverse.exception.AccountErrorCode;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.repository.AccountRepository;
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
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    AccountRepository accountRepository;
    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;


    @Override
    public AccountSimpleResponse createAccount(RegisterAccountRequest request) {
        //kiem tra emial da duoc dung chuwa
        if ( accountRepository.findByEmail(request.getEmail().trim()).isPresent()) {
            throw new AppException(AccountErrorCode.EMAIL_USED);
        }

        log.info("Create new account: {}", request);

        Role role = roleRepository.findByName(ERole.valueOf(request.getRoleName().trim().toUpperCase()))
                .orElseThrow(() -> new AppException(AccountErrorCode.ROLE_NOT_EXIST_IN_DB));

        Account account = Account.builder()
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role).build();
        accountRepository.save(account);
        return new AccountSimpleResponse(request.getEmail(), role.getName().name());
    }

}
