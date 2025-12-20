package com.volunteerhub.configuration.security.permission;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HasPermission {
    PermissionAction action();

    /**
     * SpEL expression to resolve an event id from method arguments.
     */
    String eventId() default "";

    /**
     * SpEL expression to resolve a post id from method arguments when an event id is not provided.
     */
    String postId() default "";
}
