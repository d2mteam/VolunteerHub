create table user_profiles
(
    user_id    uuid         not null
        primary key,
    created_at timestamp(6) not null,
    email      varchar(100)
        constraint ukdqltqkaw58m11jbov0udx8xqg
            unique,
    full_name  varchar(50)  not null,
    status     varchar(255)
        constraint user_profiles_status_check
            check ((status)::text = ANY
        ((ARRAY ['PENDING'::character varying, 'ACTIVE'::character varying, 'INACTIVE'::character varying, 'SUSPENDED'::character varying, 'BANNED'::character varying, 'DEACTIVATED'::character varying, 'LOCKED'::character varying, 'ARCHIVED'::character varying])::text[])),
    username   varchar(100) not null
        constraint uk5vlt12tabpccuckq0e84nhs4c
            unique,
    updated_at timestamp(6),
    bio        varchar(255),
    avatar_id  varchar(255)
);

alter table user_profiles
    owner to admin;

create table events
(
    event_id          bigint       not null
        primary key,
    created_at        timestamp(6) not null,
    event_description text,
    event_location    text,
    event_name        varchar(200) not null,
    event_state       varchar(255) not null,
    updated_at        timestamp(6),
    created_by        uuid
        constraint fk6dfe0ve0fje5r0pag124burr9
            references user_profiles,
    metadata          jsonb
);

alter table events
    owner to admin;

create table likes
(
    like_id     bigint       not null
        primary key,
    created_at  timestamp(6) not null,
    target_type varchar(255)
        constraint likes_target_type_check
            check ((target_type)::text = ANY
        ((ARRAY ['COMMENT'::character varying, 'POST'::character varying, 'EVENT'::character varying, 'LIKE'::character varying])::text[])),
    target_id   bigint,
    created_by  uuid
        constraint fkp2a6gdawsoxi0g9207cnhbe8g
            references user_profiles
);

alter table likes
    owner to admin;

create table posts
(
    post_id    bigint       not null
        primary key,
    content    text         not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    created_by uuid
        constraint fklna1w38qag86tkf2rnr6tlwi7
            references user_profiles,
    event_id   bigint
        constraint fktnoimyc2wv6tiasoiioxy0rnq
            references events,
    metadata   jsonb
);

alter table posts
    owner to admin;

create table comments
(
    comment_id bigint       not null
        primary key,
    content    text         not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    created_by uuid
        constraint fkecka9ukasgv47r4fsqvia2jfa
            references user_profiles,
    post_id    bigint
        constraint fkh4c7lvsc298whoyd4w9ta25cr
            references posts,
    metadata   jsonb
);

alter table comments
    owner to admin;

create table role_in_event
(
    id                   bigint       not null
        primary key,
    created_at           timestamp(6) not null,
    event_role           varchar(255),
    updated_at           timestamp(6),
    event_id             bigint
        constraint fkfnjhn8jf75tdvkyr43kq5wsk4
            references events,
    user_profile_id      uuid
        constraint fk5ikt6w2lkqjasjtwhj638djgi
            references user_profiles,
    participation_status varchar(255),
    constraint ukrhap0m4gtbixvvoe16i1gt5mb
        unique (user_profile_id, event_id)
);

alter table role_in_event
    owner to admin;

create table event_registration
(
    registration_id bigint       not null
        primary key,
    status          varchar(255) not null
        constraint event_registration_status_check
            check ((status)::text = ANY
        ((ARRAY ['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CANCELLED_BY_USER'::character varying])::text[])),
    event_id        bigint       not null
        constraint fkotr797f33uue7c7dtu72c8q5i
            references events,
    user_id         uuid         not null
        constraint fkbr1ftuu28f8ehfs0bhrqggli0
            references user_profiles,
    updated_at      timestamp(6),
    created_at      timestamp(6)
);

alter table event_registration
    owner to admin;

create index idx_user_event_status
    on event_registration (user_id, event_id, status);

create index idx_user_event
    on event_registration (user_id, event_id);

create table user_auth
(
    user_id        uuid         not null
        primary key,
    email          varchar(100) not null
        constraint uniq_email
            unique,
    email_verified boolean      not null,
    password_hash  text         not null,
    role           varchar(30)  not null
        constraint user_auth_role_check
            check ((role)::text = ANY
        ((ARRAY ['USER'::character varying, 'EVENT_MANAGER'::character varying, 'ADMIN'::character varying])::text[])),
    status         varchar(30)  not null
        constraint user_auth_status_check
            check ((status)::text = ANY
                   ((ARRAY ['ACTIVE'::character varying, 'DISABLED'::character varying, 'LOCKED'::character varying])::text[]))
);

alter table user_auth
    owner to admin;

