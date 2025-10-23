package com.volunteerhub.community.controller;

import com.volunteerhub.community.dto.output.Result;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class LikeMutationResolver {

    @MutationMapping
    public Result like(@Argument LikeInput input) {
        return null;
    }

    @MutationMapping
    public Result unlike(@Argument LikeInput input) {
        return null;
    }
}