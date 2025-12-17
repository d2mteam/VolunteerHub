package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.rest.request.EventVolunteerExportRequest;
import com.volunteerhub.community.service.read_service.EventVolunteerExportService;
import com.volunteerhub.community.service.read_service.EventVolunteerExportService.EventVolunteerExportResult;
import com.volunteerhub.community.service.read_service.EventVolunteerExportService.ExportFormat;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exports")
@RequiredArgsConstructor
public class ExportController {

    private final EventVolunteerExportService eventVolunteerExportService;

    @PostMapping("/event-volunteers")
    @PreAuthorize(RolePermission.ADMIN)
    public ResponseEntity<?> exportEventVolunteers(@Valid @RequestBody EventVolunteerExportRequest request) {
        EventVolunteerExportResult result = eventVolunteerExportService.export(request);

        if (result.format() == ExportFormat.CSV) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=event-volunteers.csv")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(result.csvContent());
        }

        return ResponseEntity.ok(result.rows());
    }
}
