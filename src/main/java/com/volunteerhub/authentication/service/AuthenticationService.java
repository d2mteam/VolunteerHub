package com.volunteerhub.authentication.service;

import com.volunteerhub.authentication.dtos.request.LoginRequest;
import com.volunteerhub.authentication.dtos.request.RefreshRequest;
import com.volunteerhub.authentication.dtos.request.RegistrationRequest;
import com.volunteerhub.authentication.dtos.response.LoginResponse;
import com.volunteerhub.authentication.dtos.response.RefreshResponse;

public interface AuthenticationService {
    RefreshResponse refresh(RefreshRequest request);
    LoginResponse login(LoginRequest request);
    void register(RegistrationRequest request);
}
