// com.prm392.be.labverse.security.SecurityUtils
package com.prm392.be.labverse.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static String currentUserId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return null;
        Object p = a.getPrincipal();
        if (p instanceof com.prm392.be.labverse.security.UserDetailsImpl u) {
            return u.getUserId();
        }
        // fallback: nếu principal chỉ là username (email), tuỳ hệ thống của bạn mà map sang id
        return null;
    }

    public static String currentEmail() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return null;
        Object p = a.getPrincipal();
        if (p instanceof com.prm392.be.labverse.security.UserDetailsImpl u) {
            return u.getEmail();
        }
        if (p instanceof String s) return s; // đôi khi principal là username (email)
        return null;
    }
}
