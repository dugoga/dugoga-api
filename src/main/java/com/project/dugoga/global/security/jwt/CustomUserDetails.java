package com.project.dugoga.global.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@ToString
@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String name;
    private final String nickname;
    private final String email;
    private final UserRoleEnum userRole;

    @JsonIgnore
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.userRole = user.getUserRole();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public String getPassword() { return password; }

    @Override public String getUsername() { return nickname; }

    @Override public boolean isAccountNonExpired() { return true; }

    @Override public boolean isAccountNonLocked() { return true; }

    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override public boolean isEnabled() { return true; }
}
