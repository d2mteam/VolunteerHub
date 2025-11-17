package com.volunteerhub.community.controller.rest.system_admin;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/system-admin/export")
public class AdminExportController {

    @GetMapping("/csv/download")
    public ResponseEntity<byte[]> downloadCsv() {
        return null;
    }
}
