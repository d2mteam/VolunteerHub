package com.volunteerhub.export_data;

import com.volunteerhub.community.model.db_enum.EventRole;
import com.volunteerhub.community.model.db_enum.EventState;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventVolunteerExportService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final Map<String, String> FIELD_MAPPINGS = Map.ofEntries(
            Map.entry("eventId", "e.event_id"),
            Map.entry("eventName", "e.event_name"),
            Map.entry("eventDescription", "e.event_description"),
            Map.entry("eventLocation", "e.event_location"),
            Map.entry("eventState", "e.event_state"),
            Map.entry("eventMetadata", "e.metadata"),
            Map.entry("eventCreatedAt", "e.created_at"),
            Map.entry("eventUpdatedAt", "e.updated_at"),
            Map.entry("createdBy", "e.created_by"),
            Map.entry("roleId", "r.id"),
            Map.entry("eventRole", "r.event_role"),
            Map.entry("participationStatus", "r.participation_status"),
            Map.entry("roleCreatedAt", "r.created_at"),
            Map.entry("roleUpdatedAt", "r.updated_at"),
            Map.entry("userId", "u.user_id"),
            Map.entry("username", "u.username"),
            Map.entry("fullName", "u.full_name"),
            Map.entry("email", "u.email"),
            Map.entry("userStatus", "u.status"),
            Map.entry("userCreatedAt", "u.created_at"),
            Map.entry("userUpdatedAt", "u.updated_at")
    );

    public EventVolunteerExportResult export(EventVolunteerExportRequest request) {
        List<String> requestedFields = validateFields(request.getFields());
        ExportFormat format = ExportFormat.fromString(request.getFormat());

        String sql = buildQuery(requestedFields, request);
        MapSqlParameterSource parameters = buildParameters(request);
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            for (String field : requestedFields) {
                row.put(field, rs.getObject(field));
            }
            return row;
        });

        String csvContent = format == ExportFormat.CSV ? convertToCsv(requestedFields, rows) : null;
        return new EventVolunteerExportResult(requestedFields, rows, csvContent, format);
    }

    private List<String> validateFields(List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field selection cannot be empty");
        }

        Set<String> uniqueFields = new LinkedHashSet<>(fields);
        List<String> invalidFields = uniqueFields.stream()
                .filter(field -> !FIELD_MAPPINGS.containsKey(field))
                .toList();

        if (!invalidFields.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid fields requested: " + String.join(", ", invalidFields));
        }

        return new ArrayList<>(uniqueFields);
    }

    private String buildQuery(List<String> fields, EventVolunteerExportRequest request) {
        String selectClause = fields.stream()
                .map(field -> FIELD_MAPPINGS.get(field) + " AS \"" + field + "\"")
                .collect(Collectors.joining(", "));

        StringBuilder sql = new StringBuilder()
                .append("SELECT ").append(selectClause)
                .append(" FROM events e \n")
                .append("JOIN role_in_event r ON e.event_id = r.event_id \n")
                .append("JOIN user_profiles u ON u.user_id = r.user_profile_id \n")
                .append("WHERE 1=1 ");

        if (request.getEventIds() != null && !request.getEventIds().isEmpty()) {
            sql.append("AND e.event_id IN (:eventIds) ");
        }

        if (request.getEventRoles() != null && !request.getEventRoles().isEmpty()) {
            sql.append("AND r.event_role IN (:eventRoles) ");
        }

        if (request.getParticipationStatuses() != null && !request.getParticipationStatuses().isEmpty()) {
            sql.append("AND r.participation_status IN (:participationStatuses) ");
        }

        if (request.getEventStates() != null && !request.getEventStates().isEmpty()) {
            sql.append("AND e.event_state IN (:eventStates) ");
        }

        sql.append("ORDER BY e.event_id, r.id");
        return sql.toString();
    }

    private MapSqlParameterSource buildParameters(EventVolunteerExportRequest request) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (request.getEventIds() != null && !request.getEventIds().isEmpty()) {
            parameters.addValue("eventIds", request.getEventIds());
        }

        if (request.getEventRoles() != null && !request.getEventRoles().isEmpty()) {
            parameters.addValue("eventRoles", request.getEventRoles().stream().map(EventRole::name).toList());
        }

        if (request.getParticipationStatuses() != null && !request.getParticipationStatuses().isEmpty()) {
            parameters.addValue("participationStatuses", request.getParticipationStatuses().stream()
                    .map(ParticipationStatus::name)
                    .toList());
        }

        if (request.getEventStates() != null && !request.getEventStates().isEmpty()) {
            parameters.addValue("eventStates", request.getEventStates().stream().map(EventState::name).toList());
        }

        return parameters;
    }

    private String convertToCsv(List<String> fields, List<Map<String, Object>> rows) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append(String.join(",", fields)).append("\n");

        for (Map<String, Object> row : rows) {
            List<String> values = new ArrayList<>();
            for (String field : fields) {
                Object value = row.get(field);
                values.add(escapeCsv(value == null ? "" : value.toString()));
            }
            csvBuilder.append(String.join(",", values)).append("\n");
        }

        return csvBuilder.toString();
    }

    private String escapeCsv(String value) {
        String escapedValue = value.replace("\"", "\"\"");
        if (escapedValue.contains(",") || escapedValue.contains("\n") || escapedValue.contains("\r")) {
            return "\"" + escapedValue + "\"";
        }
        return escapedValue;
    }

    public record EventVolunteerExportResult(List<String> fields,
                                             List<Map<String, Object>> rows,
                                             String csvContent,
                                             ExportFormat format) {
    }

    public enum ExportFormat {
        JSON, CSV;

        public static ExportFormat fromString(String value) {
            if (value == null) {
                return JSON;
            }

            return switch (value.toLowerCase()) {
                case "csv" -> CSV;
                case "json" -> JSON;
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unsupported export format: " + value);
            };
        }
    }
}
