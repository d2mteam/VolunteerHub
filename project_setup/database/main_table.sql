CREATE SEQUENCE user_auth_provider_seq
    INCREMENT BY 50;

CREATE TABLE user_profiles
(
    user_id     UUID PRIMARY KEY,
    avatar_url  VARCHAR(255),
    created_at  TIMESTAMP(6) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    full_name   VARCHAR(50)  NOT NULL,
    system_role VARCHAR(255),
    status      VARCHAR(255),
    username    VARCHAR(100) NOT NULL UNIQUE,
    updated_at  TIMESTAMP(6),
    bio         VARCHAR(255),
    CONSTRAINT user_profiles_system_role_check CHECK (system_role IN (
                                                               'ROLE_VOLUNTEER',
                                                               'ROLE_EVENT_MANAGER',
                                                               'ROLE_ADMIN'
        )),
    CONSTRAINT user_profiles_status_check CHECK (status IN (
                                                            'PENDING',
                                                            'ACTIVE',
                                                            'INACTIVE',
                                                            'SUSPENDED',
                                                            'BANNED',
                                                            'DEACTIVATED',
                                                            'LOCKED',
                                                            'ARCHIVED'
        ))
);

CREATE TABLE events
(
    event_id          BIGINT PRIMARY KEY,
    created_at        TIMESTAMP(6) NOT NULL,
    event_description TEXT,
    event_location    TEXT,
    event_name        VARCHAR(200) NOT NULL,
    event_state       VARCHAR(255) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by        UUID REFERENCES user_profiles (user_id)
);

CREATE TABLE likes
(
    like_id     BIGINT PRIMARY KEY,
    created_at  TIMESTAMP(6) NOT NULL,
    target_type VARCHAR(255),
    target_id   BIGINT,
    created_by  UUID REFERENCES user_profiles (user_id),
    CONSTRAINT likes_target_type_check CHECK (target_type IN (
                                                              'COMMENT', 'POST', 'EVENT', 'LIKE'
        ))
);

CREATE TABLE posts
(
    post_id    BIGINT PRIMARY KEY,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6),
    created_by UUID REFERENCES user_profiles (user_id),
    event_id   BIGINT REFERENCES events (event_id)
);

CREATE TABLE comments
(
    comment_id BIGINT PRIMARY KEY,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6),
    created_by UUID REFERENCES user_profiles (user_id),
    post_id    BIGINT REFERENCES posts (post_id)
);

CREATE TABLE role_in_event
(
    id                   BIGINT PRIMARY KEY,
    created_at           TIMESTAMP(6) NOT NULL,
    event_role           VARCHAR(255),
    updated_at           TIMESTAMP(6),
    event_id             BIGINT REFERENCES events (event_id),
    user_profile_id      UUID REFERENCES user_profiles (user_id),
    participation_status VARCHAR(255),
    UNIQUE (user_profile_id, event_id)
);

CREATE TABLE event_registration
(
    registration_id BIGINT PRIMARY KEY,
    status          VARCHAR(255) NOT NULL,
    event_id        BIGINT       NOT NULL REFERENCES events (event_id),
    user_id         UUID         NOT NULL REFERENCES user_profiles (user_id),
    updated_at      TIMESTAMP(6),
    created_at      TIMESTAMP(6),
    CONSTRAINT event_registration_status_check CHECK (status IN (
                                                                 'PENDING', 'APPROVED', 'REJECTED', 'CANCELLED_BY_USER'
        ))
);

CREATE INDEX idx_user_event
    ON event_registration (user_id, event_id);

CREATE TABLE user_auth_provider
(
    id               BIGINT PRIMARY KEY,
    provider         VARCHAR(255),
    provider_user_id VARCHAR(255),
    user_profile_id  UUID NOT NULL REFERENCES user_profiles (user_id),
    UNIQUE (provider, provider_user_id)
);