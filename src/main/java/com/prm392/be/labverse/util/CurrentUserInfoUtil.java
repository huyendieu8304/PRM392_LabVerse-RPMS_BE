package com.prm392.be.labverse.util;

import com.prm392.be.labverse.security.CurrentUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This is a util class
 * <p>
 *     current purpose of this class just is get out the information of current authenticated user
 * </p>
 */
public class CurrentUserInfoUtil {

    private static CurrentUserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CurrentUserInfo userInfo) {
            return userInfo;
        }

        return null;
    }

    /**
     * Use this method to the  user  id of current login user
     * @return a String of user id
     */
    public static String getCurrentUserId() {
        CurrentUserInfo user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * Use this method to the email of current loggin user
     * @return a String of user's email
     */
    public static String getCurrentUserEmail() {
        CurrentUserInfo user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public static String getCurrentUserRoles() {
        CurrentUserInfo user = getCurrentUser();
        return user != null ? user.getRoles() : null;
    }
}
