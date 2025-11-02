package com.volunteerhub.community.permission;

public class PolicyContextHolder {
    private static final ThreadLocal<PolicyContext> contextHolder = new ThreadLocal<>();

    private PolicyContextHolder() {
    }

    public static PolicyContext getContext() {
        PolicyContext context = contextHolder.get();
        if (context == null) {
            context = new PolicyContext();
            contextHolder.set(context);
        }
        return context;
    }

    public static void setContext(PolicyContext context) {
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
