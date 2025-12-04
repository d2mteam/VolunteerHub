package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
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
    public ActionResponse<Void>  createEvent(UUID userId, CreateEventInput input) {
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

        LocalDateTime now = LocalDateTime.now();

        return ActionResponse.success(
                event.getEventId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void>  editEvent(UUID userId, EditEventInput input) {
        Optional<Event> optional = eventRepository.findById(input.getEventId());
        if (optional.isEmpty()) {
            return ActionResponse.failure("Event not found");
        }

        Event event = optional.get();
        event.setEventName(input.getEventName());
        event.setEventDescription(input.getEventDescription());
        event.setEventLocation(input.getEventLocation());
        eventRepository.save(event);

        return ActionResponse.success(
                event.getEventId().toString(),
                null,
                LocalDateTime.now()
        );
    }

    @Override
    public ActionResponse<Void>  deleteEvent(UUID userId, Long eventId) {
        boolean exists = eventRepository.existsById(eventId);
        if (!exists) {
            return ActionResponse.failure("Event with id " + eventId + " does not exist");
        }

        eventRepository.deleteById(eventId);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                eventId.toString(),
                null,
                now
        );
    }
}
