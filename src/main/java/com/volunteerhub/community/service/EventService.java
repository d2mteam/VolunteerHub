package com.volunteerhub.community.service;

import com.volunteerhub.community.dto.input.CreateEventInput;
import com.volunteerhub.community.dto.input.EditEventInput;
import com.volunteerhub.community.dto.output.EventDto;

public interface EventService {
    EventDto createEvent(CreateEventInput input);
    EventDto editEvent(EditEventInput input);
    void delete(Long id);
}
