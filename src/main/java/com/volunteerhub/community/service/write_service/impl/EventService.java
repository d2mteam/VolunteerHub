package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.entity.Event;
import com.volunteerhub.community.entity.EventState;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IEventService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class EventService implements IEventService {
    private final EventRepository eventRepository;
    private final UserProfileRepository userProfileService;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public void moderate(UUID userId, List<Long> eventIds) {

    }

    @Override
    public void createEvent(UUID userId, CreateEventInput input) {
        UserProfile userProfile = userProfileService.getReferenceById(userId);
        eventRepository.save(Event.builder()
                .eventId(idGenerator.nextId())
                .eventDescription(input.getEventDescription())
                .eventLocation(input.getEventLocation())
                .eventName(input.getEventName())
                .eventState(EventState.Pending)
                .createdBy(userProfile)
                .build());
    }

    @Override
    public void editEvent(UUID userId, EditEventInput input) {
        eventRepository.findById(input.getEventId()).ifPresent(event -> {
            event.setEventName(input.getEventName());
            event.setEventDescription(input.getEventDescription());
            event.setEventLocation(input.getEventLocation());
        });
    }

    @Override
    public void deleteEvent(UUID userId, Long eventId) {
        eventRepository.deleteById(eventId);
    }
}
