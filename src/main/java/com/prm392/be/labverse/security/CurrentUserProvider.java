package com.prm392.be.labverse.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public CurrentUserInfo get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object p = auth.getPrincipal();
        if (p instanceof CurrentUserInfo info) return info;

        // fallback nếu ai đó set principal kiểu khác
        return null;
    }
}
