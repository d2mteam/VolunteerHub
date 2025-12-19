CREATE TABLE user_auth
(
    user_id        uuid         NOT NULL PRIMARY KEY,
    email          varchar(100) NOT NULL,
    email_verified boolean      NOT NULL,
    password_hash  text         NOT NULL,
    role           varchar(30)  NOT NULL,
    status         varchar(30)  NOT NULL
);

CREATE TABLE user_profiles
(
    user_id    uuid         NOT NULL PRIMARY KEY,
    avatar_id  varchar(255),
    bio        text,
    created_at timestamp(6) NOT NULL,
    email      varchar(100),
    full_name  varchar(100) NOT NULL,
    status     varchar(255) NOT NULL,
    updated_at timestamp(6) NOT NULL,
    username   varchar(100) NOT NULL
);

CREATE TABLE events
(
    event_id          bigint       NOT NULL PRIMARY KEY,
    created_at        timestamp(6) NOT NULL,
    event_description text,
    event_location    text,
    event_name        varchar(200) NOT NULL,
    event_state       varchar(255) NOT NULL,
    metadata          jsonb,
    updated_at        timestamp(6),
    created_by        uuid,
    CONSTRAINT fk_events_created_by
        FOREIGN KEY (created_by) REFERENCES user_profiles (user_id)
);

CREATE TABLE event_registration
(
    registration_id bigint       NOT NULL PRIMARY KEY,
    created_at      timestamp(6) NOT NULL,
    status          varchar(255) NOT NULL,
    updated_at      timestamp(6),
    event_id        bigint       NOT NULL,
    user_id         uuid         NOT NULL,
    CONSTRAINT fk_event_registration_event
        FOREIGN KEY (event_id) REFERENCES events (event_id),
    CONSTRAINT fk_event_registration_user
        FOREIGN KEY (user_id) REFERENCES user_profiles (user_id)
);

CREATE TABLE likes
(
    like_id     bigint       NOT NULL PRIMARY KEY,
    created_at  timestamp(6) NOT NULL,
    target_type varchar(255),
    target_id   bigint,
    created_by  uuid
);

CREATE TABLE posts
(
    post_id    bigint       NOT NULL PRIMARY KEY,
    content    text         NOT NULL,
    created_at timestamp(6) NOT NULL,
    metadata   jsonb,
    updated_at timestamp(6),
    created_by uuid,
    event_id   bigint,
    CONSTRAINT fk_posts_created_by
        FOREIGN KEY (created_by) REFERENCES user_profiles (user_id),
    CONSTRAINT fk_posts_event
        FOREIGN KEY (event_id) REFERENCES events (event_id)
);

CREATE TABLE comments
(
    comment_id bigint       NOT NULL PRIMARY KEY,
    content    text         NOT NULL,
    created_at timestamp(6) NOT NULL,
    metadata   jsonb,
    updated_at timestamp(6),
    created_by uuid,
    post_id    bigint,
    CONSTRAINT fk_comments_created_by
        FOREIGN KEY (created_by) REFERENCES user_profiles (user_id),
    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id) REFERENCES posts (post_id)
);

CREATE TABLE role_in_event
(
    id                   bigint       NOT NULL PRIMARY KEY,
    created_at           timestamp(6) NOT NULL,
    event_role           varchar(255) NOT NULL,
    participation_status varchar(255) NOT NULL,
    updated_at           timestamp(6),
    event_id             bigint,
    user_profile_id      uuid,
    CONSTRAINT fk_role_in_event_event
        FOREIGN KEY (event_id) REFERENCES events (event_id),
    CONSTRAINT fk_role_in_event_user
        FOREIGN KEY (user_profile_id) REFERENCES user_profiles (user_id)
);