create table app_user
(
    id         uuid      default gen_random_uuid() not null
        constraint member_pkey
            primary key,
    username   varchar(50)                         not null,
    password   varchar(1000)                       not null,
    email      varchar(100)                        not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP,
    constraint uq_member_username
        unique (username, email)
);

alter table app_user
    owner to root;

create table achievements
(
    id                 uuid        default gen_random_uuid()          not null
        primary key,
    user_id            uuid                                           not null
        constraint achievements_app_user_id_fk
            references app_user,
    title              text                                           not null,
    org_name           text,
    duration_start     date                                           not null,
    duration_end       date,
    impact_summary     text,
    status             varchar(20) default 'DRAFT'::character varying not null,
    created_at         timestamp   default now()                      not null,
    updated_at         timestamp   default now()                      not null,
    skills             text,
    role_title         varchar(255),
    work_type          varchar(20),
    contribution_level varchar(20),
    goal_summary       text
);

comment on column achievements.title is '제목';

comment on column achievements.org_name is '소속';

comment on column achievements.duration_start is '기간 시작';

comment on column achievements.duration_end is '기간 종료';

comment on column achievements.impact_summary is '핵심 성과 요약';

comment on column achievements.status is '상태';

comment on column achievements.skills is '기술 목록';

comment on column achievements.role_title is '역할';

comment on column achievements.work_type is '일의 유형';

comment on column achievements.contribution_level is '기여 수준';

comment on column achievements.goal_summary is '목표 요약';

alter table achievements
    owner to root;

create table achievement_sections
(
    id             uuid        default gen_random_uuid()         not null
        primary key,
    achievement_id uuid                                          not null
        references achievements
            on delete cascade,
    kind           varchar(20) default 'NONE'::character varying not null,
    title          varchar(255)                                  not null,
    content        text                                          not null,
    sort_order     integer     default 0                         not null,
    created_at     timestamp   default now()                     not null,
    updated_at     timestamp   default now()                     not null
);

alter table achievement_sections
    owner to root;

create table user_action_log
(
    id             bigserial
        primary key,
    created_at     timestamp default now() not null,
    event_category varchar(50)             not null,
    event_name     varchar(100)            not null,
    ip_address     varchar(50),
    page_url       varchar(500),
    properties     text,
    referrer       varchar(500),
    session_id     varchar(100),
    user_agent     varchar(500),
    user_id        varchar(100)
);

alter table user_action_log
    owner to root;

create index idx_user_action_log_event_name
    on user_action_log (event_name);

create index idx_user_action_log_user_id
    on user_action_log (user_id);

create index idx_user_action_log_session_id
    on user_action_log (session_id);

