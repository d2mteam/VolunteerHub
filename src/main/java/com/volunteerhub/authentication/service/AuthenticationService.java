package com.volunteerhub.authentication.service;

import com.volunteerhub.authentication.dto.request.LoginRequest;
import com.volunteerhub.authentication.dto.request.RefreshRequest;
import com.volunteerhub.authentication.dto.request.RegistrationRequest;
import com.volunteerhub.authentication.dto.response.LoginResponse;
import com.volunteerhub.authentication.dto.response.RefreshResponse;

public interface AuthenticationService {
    RefreshResponse refresh(RefreshRequest request);
    LoginResponse login(LoginRequest request);
    void register(RegistrationRequest request);
}
