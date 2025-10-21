package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.input.CreateEventInput;
import com.volunteerhub.community.dto.input.EditEventInput;

public interface IEventWriteService {
    void deleteEventById(Long id);
    void createEvent(CreateEventInput input);
    void editEvent(EditEventInput input);
}
