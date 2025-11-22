package com.payment.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.payment.model.Permission.*;

public enum Role {
    USER(Set.of(
            PAYMENT_READ,
            PAYMENT_CREATE
    )),
    ADMIN(Set.of(
            PAYMENT_READ,
            PAYMENT_CREATE,
            PAYMENT_UPDATE,
            PAYMENT_DELETE,
            PAYMENT_REFUND
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
