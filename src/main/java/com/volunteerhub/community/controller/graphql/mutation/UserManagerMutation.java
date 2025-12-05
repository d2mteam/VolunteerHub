package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.service.write_service.IUserManagerService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserManagerMutation {
    private final IUserManagerService userManagerService;

    @MutationMapping
    public ActionResponse<Void> banUser(@Argument UUID userId) {
        return userManagerService.banUser(userId);
    }

    @MutationMapping
    public ActionResponse<Void> unbanUser(@Argument UUID userId) {
        return userManagerService.unbanUser(userId);
    }
}
