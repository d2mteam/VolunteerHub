package com.volunteerhub.community.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volunteerhub.community.model.graphql.ExportFormat;
import com.volunteerhub.community.model.graphql.ExportPayload;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final EventRepository eventRepository;
    private final UserProfileRepository userProfileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExportPayload exportEvents(ExportFormat format) {
        List<Map<String, Object>> rows = eventRepository.findAll().stream()
                .map(event -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("eventId", event.getEventId());
                    row.put("eventName", event.getEventName());
                    row.put("eventLocation", event.getEventLocation());
                    row.put("eventState", event.getEventState());
                    row.put("createdAt", formatDate(event.getCreatedAt()));
                    return row;
                })
                .collect(Collectors.toList());

        return buildPayload("events", format, rows);
    }

    public ExportPayload exportVolunteers(ExportFormat format) {
        List<Map<String, Object>> rows = userProfileRepository.findAll().stream()
                .map(profile -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("userId", profile.getUserId());
                    row.put("username", profile.getUsername());
                    row.put("fullName", profile.getFullName());
                    row.put("email", profile.getEmail());
                    row.put("status", profile.getStatus());
                    row.put("createdAt", formatDate(profile.getCreatedAt()));
                    return row;
                })
                .collect(Collectors.toList());

        return buildPayload("volunteers", format, rows);
    }

    private ExportPayload buildPayload(String prefix, ExportFormat format, List<Map<String, Object>> rows) {
        return switch (format) {
            case CSV -> ExportPayload.builder()
                    .filename(prefix + ".csv")
                    .contentType("text/csv")
                    .data(toCsv(rows))
                    .build();
            case JSON -> ExportPayload.builder()
                    .filename(prefix + ".json")
                    .contentType("application/json")
                    .data(toJson(rows))
                    .build();
        };
    }

    private String toCsv(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return "";
        }
        List<String> headers = rows.get(0).keySet().stream().toList();
        StringBuilder builder = new StringBuilder(String.join(",", headers)).append("\n");
        for (Map<String, Object> row : rows) {
            StringJoiner joiner = new StringJoiner(",");
            for (String header : headers) {
                Object value = row.get(header);
                joiner.add(value == null ? "" : escapeCsv(value.toString()));
            }
            builder.append(joiner).append("\n");
        }
        return builder.toString();
    }

    private String toJson(List<Map<String, Object>> rows) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rows);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to export JSON", e);
        }
    }

    private String escapeCsv(String value) {
        boolean hasSpecial = value.contains(",") || value.contains("\n") || value.contains("\"");
        if (!hasSpecial) {
            return value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String formatDate(java.time.LocalDateTime dateTime) {
        return dateTime == null ? null : DATE_TIME_FORMATTER.format(dateTime);
    }
}
