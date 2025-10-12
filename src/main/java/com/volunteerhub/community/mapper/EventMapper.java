package com.volunteerhub.community.mapper;

import com.volunteerhub.community.dto.output.EventDto;
import com.volunteerhub.community.entity.Event;

public class EventMapper {
    public static Event toEntity(EventDto dto) {
        return Event.builder()
                .eventId(dto.getEventId())
                .eventName(dto.getEventName())
                .updatedAt(dto.getUpdatedAt())
                .createdAt(dto.getCreatedAt())
                .eventLocation(dto.getEventLocation())
                .eventDescription(dto.getEventDescription())
                .build();
    }

    public static EventDto toDto(Event entity) {
        return EventDto.builder()
                .eventId(entity.getEventId())
                .eventName(entity.getEventName())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .eventLocation(entity.getEventLocation())
                .eventDescription(entity.getEventDescription())
                .build();
    }
}
