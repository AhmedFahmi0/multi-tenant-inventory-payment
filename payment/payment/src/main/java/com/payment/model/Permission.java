package com.payment.model;

public enum Permission {
    PAYMENT_READ("payment:read"),
    PAYMENT_CREATE("payment:create"),
    PAYMENT_UPDATE("payment:update"),
    PAYMENT_DELETE("payment:delete"),
    PAYMENT_REFUND("payment:refund");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
