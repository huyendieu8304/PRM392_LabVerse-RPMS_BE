package com.prm392.be.labverse.security;

import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AuthErrorCode;
import com.prm392.be.labverse.repository.UserRepository;
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

    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user;
        //get the account from the repository
        user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(AuthErrorCode.ACCOUNT_NOT_EXIST.getMessage()));

//        if (!account.isActive() || !account.isEmailVerified()) {
//            //the account of user has been banned
//            throw new InternalAuthenticationServiceException(ErrorCode.ACCOUNT_IS_INACTIVE.getMessage());
//        }
        //build UserDetails object
        return UserDetailsImpl.build(user);
    }
}
