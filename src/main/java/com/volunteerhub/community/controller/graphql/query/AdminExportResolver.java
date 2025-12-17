package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.graphql.ExportFormat;
import com.volunteerhub.community.model.graphql.ExportPayload;
import com.volunteerhub.community.service.ExportService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class AdminExportResolver {
    private final ExportService exportService;

    @QueryMapping
    public ExportPayload exportEvents(@Argument ExportFormat format) {
        return exportService.exportEvents(format);
    }

    @QueryMapping
    public ExportPayload exportVolunteers(@Argument ExportFormat format) {
        return exportService.exportVolunteers(format);
    }
}
