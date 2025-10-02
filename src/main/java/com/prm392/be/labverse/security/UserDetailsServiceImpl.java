package com.prm392.be.labverse.security;

import com.prm392.be.labverse.entity.Account;
import com.prm392.be.labverse.exception.AuthErrorCode;
import com.prm392.be.labverse.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = null;
        //get the account from the repository
        account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(AuthErrorCode.ACCOUNT_NOT_EXIST.getMessage()));

//        if (!account.isActive() || !account.isEmailVerified()) {
//            //the account of user has been banned
//            throw new InternalAuthenticationServiceException(ErrorCode.ACCOUNT_IS_INACTIVE.getMessage());
//        }
        //build UserDetails object
        return UserDetailsImpl.build(account);
    }
}
