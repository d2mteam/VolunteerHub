package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.service.user_service.EventRegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class EventRegistrationMutation {
    private EventRegistrationService eventRegistrationService;
}
