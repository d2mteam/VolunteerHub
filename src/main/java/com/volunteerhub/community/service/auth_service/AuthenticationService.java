package com.volunteerhub.community.service.auth_service;

import com.volunteerhub.community.dto.rest.request.LoginRequest;
import com.volunteerhub.community.dto.rest.request.RefreshRequest;
import com.volunteerhub.community.dto.rest.request.RegistrationRequest;
import com.volunteerhub.community.dto.rest.response.LoginResponse;
import com.volunteerhub.community.dto.rest.response.RefreshResponse;

public interface AuthenticationService {
    RefreshResponse refresh(RefreshRequest request);
    LoginResponse login(LoginRequest request);
    void register(RegistrationRequest request);
}
