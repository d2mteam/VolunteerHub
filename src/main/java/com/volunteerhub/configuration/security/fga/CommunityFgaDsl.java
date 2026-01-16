package com.volunteerhub.configuration.security.fga;

public final class CommunityFgaDsl {

    private CommunityFgaDsl() {}

    public static final String MODEL = """
            type user
              relations
                self: user

            type event
              relations
                owner: user
                member: user
                admin: user
                viewer: owner | member | admin
                poster: member | admin
                commenter: member | admin

            type post
              relations
                owner: user
                event: event
                viewer: owner | event.viewer
                commenter: owner | event.commenter

            type comment
              relations
                owner: user
                post: post
                viewer: owner | post.viewer
            """;
}
