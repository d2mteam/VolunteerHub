package com.volunteerhub.authentication.controller;

import com.volunteerhub.authentication.dto.request.CreateAdminRequest;
import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.service.AdminAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {
    private final AdminAccountService adminAccountService;

    @PostMapping
    @PreAuthorize(RolePermission.ADMIN)
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        UserAuth admin = adminAccountService.createAdmin(request);
        return ResponseEntity.ok(Map.of(
                "userId", admin.getUserId(),
                "email", admin.getEmail()
        ));
    }
}
