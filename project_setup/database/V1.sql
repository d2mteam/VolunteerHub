CREATE TABLE user_profiles (
                               user_id    UUID PRIMARY KEY,
                               created_at TIMESTAMP(6) NOT NULL,
                               email      VARCHAR(100) UNIQUE,
                               full_name  VARCHAR(50) NOT NULL,
                               status     VARCHAR(255) CHECK (status IN (
                                                                         'PENDING','ACTIVE','INACTIVE','SUSPENDED','BANNED','DEACTIVATED','LOCKED','ARCHIVED')),
                               username   VARCHAR(100) UNIQUE NOT NULL,
                               updated_at TIMESTAMP(6),
                               bio        VARCHAR(255),
                               avatar_id  VARCHAR(255)
);

CREATE TABLE events (
                        event_id          BIGINT PRIMARY KEY,
                        created_at        TIMESTAMP(6) NOT NULL,
                        event_description TEXT,
                        event_location    TEXT,
                        event_name        VARCHAR(200) NOT NULL,
                        event_state       VARCHAR(255) NOT NULL,
                        updated_at        TIMESTAMP(6),
                        created_by        UUID REFERENCES user_profiles(user_id),
                        metadata          JSONB
);

CREATE TABLE likes (
                       like_id     BIGINT PRIMARY KEY,
                       created_at  TIMESTAMP(6) NOT NULL,
                       target_type VARCHAR(255) CHECK (target_type IN ('COMMENT','POST','EVENT','LIKE')),
                       target_id   BIGINT,
                       created_by  UUID REFERENCES user_profiles(user_id)
);

CREATE TABLE posts (
                       post_id    BIGINT PRIMARY KEY,
                       content    TEXT NOT NULL,
                       created_at TIMESTAMP(6) NOT NULL,
                       updated_at TIMESTAMP(6),
                       created_by UUID REFERENCES user_profiles(user_id),
                       event_id   BIGINT REFERENCES events(event_id),
                       metadata   JSONB
);

CREATE TABLE comments (
                          comment_id BIGINT PRIMARY KEY,
                          content    TEXT NOT NULL,
                          created_at TIMESTAMP(6) NOT NULL,
                          updated_at TIMESTAMP(6),
                          created_by UUID REFERENCES user_profiles(user_id),
                          post_id    BIGINT REFERENCES posts(post_id),
                          metadata   JSONB
);

CREATE TABLE role_in_event (
                               id                   BIGINT PRIMARY KEY,
                               created_at           TIMESTAMP(6) NOT NULL,
                               event_role           VARCHAR(255),
                               updated_at           TIMESTAMP(6),
                               event_id             BIGINT REFERENCES events(event_id),
                               user_profile_id      UUID REFERENCES user_profiles(user_id),
                               participation_status VARCHAR(255),
                               UNIQUE (user_profile_id, event_id)
);

CREATE TABLE event_registration (
                                    registration_id BIGINT PRIMARY KEY,
                                    status          VARCHAR(255) NOT NULL CHECK (status IN ('PENDING','APPROVED','REJECTED','CANCELLED_BY_USER')),
                                    event_id        BIGINT NOT NULL REFERENCES events(event_id),
                                    user_id         UUID NOT NULL REFERENCES user_profiles(user_id),
                                    updated_at      TIMESTAMP(6),
                                    created_at      TIMESTAMP(6)
);

CREATE INDEX idx_user_event_status ON event_registration (user_id, event_id, status);
CREATE INDEX idx_user_event        ON event_registration (user_id, event_id);

CREATE TABLE user_auth (
                           user_id        UUID PRIMARY KEY,
                           email          VARCHAR(100) NOT NULL UNIQUE,
                           email_verified BOOLEAN NOT NULL,
                           password_hash  TEXT NOT NULL,
                           role           VARCHAR(30) NOT NULL CHECK (role IN ('USER','EVENT_MANAGER','ADMIN')),
                           status         VARCHAR(30) NOT NULL CHECK (status IN ('ACTIVE','DISABLED','LOCKED'))
);
