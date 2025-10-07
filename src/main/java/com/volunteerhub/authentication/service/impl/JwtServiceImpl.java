package com.volunteerhub.authentication.service.impl;

import com.volunteerhub.authentication.config.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JwtServiceImpl  {
    private final JwtUtils jwtUtils;
}
