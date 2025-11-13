package com.prm392.be.labverse.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.entity.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsImpl implements UserDetails {

    User user;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user) {

        String roleName = (user.getRole() != null && user.getRole().getName() != null)
                ? "ROLE_" + user.getRole().getName()
                : "ROLE_USER"; // fallback role mặc định

        //create custom Authority for User
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

        return new UserDetailsImpl(
                user,
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    //this actually return the email of the user
    @Override
    public String getUsername() {
        return user.getEmail();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !user.getDeleteFlag();
    }

    public Role getRole(){
        return user.getRole();
    }

    public String getUserId(){
        return user.getId();
    }

    public String getEmail(){
        return user.getEmail();
    }

}

