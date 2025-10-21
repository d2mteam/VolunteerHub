package com.volunteerhub.ultis;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomPrincipal {
    private UUID userId;
    private String username;
//    private List<String> roles;
}
