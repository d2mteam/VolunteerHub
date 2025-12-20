package com.volunteerhub.authentication.model;

public final class RolePermission {

    private RolePermission() {
    }

    public static final String ADMIN = "hasAuthority('ADMIN')";
    public static final String USER = "hasAuthority('USER')";
    public static final String EVENT_MANAGER = "hasAuthority('EVENT_MANAGER')";
    public static final String USER_OR_EVENT_MANAGER =
            "hasAnyAuthority('USER', 'EVENT_MANAGER')";

    public static final String EVENT_MANAGER_OR_ADMIN =
            "hasAnyAuthority('ADMIN', 'EVENT_MANAGER')";
}
