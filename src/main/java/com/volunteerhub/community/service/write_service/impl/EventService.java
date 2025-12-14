package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.ModerationAction;
import com.volunteerhub.community.dto.ModerationResponse;
import com.volunteerhub.community.dto.ModerationStatus;
import com.volunteerhub.community.model.Event;
import com.volunteerhub.community.model.RoleInEvent;
import com.volunteerhub.community.model.UserProfile;
import com.volunteerhub.community.model.db_enum.EventRole;
import com.volunteerhub.community.model.db_enum.EventState;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IEventService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final RoleInEventRepository roleInEventRepository;
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public ModerationResponse approveEvent(Long eventId) {
        int result = eventRepository.updateEventStatus(eventId, EventState.ACCEPTED);

        if (result == 0) {
            return ModerationResponse.failure(
                    ModerationAction.APPROVE_EVENT,
                    "EVENT",
                    eventId.toString(),
                    "Event approval failed");
        }

        return ModerationResponse.success(
                ModerationAction.APPROVE_EVENT,
                "EVENT",
                eventId.toString(),
                ModerationStatus.APPROVED,
                "Event approved",
                LocalDateTime.now());
    }

    @Override
    public ModerationResponse createEvent(UUID userId, CreateEventInput input) {
        UserProfile creator = userProfileRepository.getReferenceById(userId);

        Event event = Event.builder()
                .eventId(idGenerator.nextId())
                .eventName(input.getEventName())
                .eventDescription(input.getEventDescription())
                .eventLocation(input.getEventLocation())
                .eventState(EventState.PENDING)
                .createdBy(creator)
                .build();

        eventRepository.save(event);

        RoleInEvent roleInEvent = RoleInEvent.builder()
                .id(idGenerator.nextId())
                .eventRole(EventRole.EVENT_ADMIN)
                .event(event)
                .userProfile(creator)
                .build();

        roleInEventRepository.save(roleInEvent);

        return ModerationResponse.success(
                ModerationAction.CREATE_EVENT,
                "EVENT",
                event.getEventId().toString(),
                ModerationStatus.CREATED,
                "Event created",
                LocalDateTime.now());
    }

    @Override
    public ModerationResponse editEvent(UUID userId, EditEventInput input) {
        Optional<Event> optional = eventRepository.findById(input.getEventId());
        if (optional.isEmpty()) {
            return ModerationResponse.failure(
                    ModerationAction.EDIT_EVENT,
                    "EVENT",
                    input.getEventId().toString(),
                    "Event not found");
        }

        Event event = optional.get();
        event.setEventName(input.getEventName());
        event.setEventDescription(input.getEventDescription());
        event.setEventLocation(input.getEventLocation());
        eventRepository.save(event);

        return ModerationResponse.success(
                ModerationAction.EDIT_EVENT,
                "EVENT",
                event.getEventId().toString(),
                ModerationStatus.UPDATED,
                "Event updated",
                LocalDateTime.now());
    }

    @Override
    public ModerationResponse deleteEvent(UUID userId, Long eventId) {
        boolean exists = eventRepository.existsById(eventId);
        if (!exists) {
            return ModerationResponse.failure(
                    ModerationAction.DELETE_EVENT,
                    "EVENT",
                    eventId.toString(),
                    String.format("Event with id %d does not exist", eventId));
        }

        eventRepository.deleteById(eventId);

        return ModerationResponse.success(
                ModerationAction.DELETE_EVENT,
                "EVENT",
                eventId.toString(),
                ModerationStatus.DELETED,
                "Event deleted",
                LocalDateTime.now());
    }
}
