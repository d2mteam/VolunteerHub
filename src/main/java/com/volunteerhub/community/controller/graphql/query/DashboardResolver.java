package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.model.entity.UserProfile;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@AllArgsConstructor
public class DashboardResolver {
    @QueryMapping
    public Event dashboardEvents(@Argument Map<String, Object> filter) {
        return null;
    }

    @QueryMapping
    public Post dashboardPosts(@Argument Map<String, Object> filter) {
        return null;
    }
}
