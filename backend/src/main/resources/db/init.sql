-- # 사용자, 구독 관련
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

create table plans
(
    id                                bigserial
        primary key,
    name                              varchar(50)                            not null,
    plan_type                         varchar(20)                            not null,
    price                             bigint                   default 0     not null,
    created_at                        timestamp with time zone default now() not null,
    updated_at                        timestamp with time zone default now() not null,
    experience_analysis_limit_per_day integer                  default 0     not null,
    resume_limit_per_month            integer                  default 0     not null,
    career_analysis_limit_per_month   integer                  default 0     not null,
    experience_analysis_model         varchar(50),
    resume_model                      varchar(50),
    career_analysis_model             varchar(50)
);

comment on column plans.name is '플랜명';

comment on column plans.plan_type is '플랜 타입 enum';

comment on column plans.price is '요금';

comment on column plans.created_at is '생성일';

comment on column plans.updated_at is '수정일';

comment on column plans.experience_analysis_limit_per_day is '경험분석 제한횟수';

comment on column plans.resume_limit_per_month is '이력서 생성 제한횟수';

comment on column plans.career_analysis_limit_per_month is '커리어분석 제한 횟수';

create table subscriptions
(
    id         uuid                     default gen_random_uuid() not null
        primary key,
    user_id    uuid                                               not null
        unique
        constraint fk_subscription_user
            references app_user,
    plan_id    bigint                                             not null
        constraint fk_subscription_plan
            references plans,
    status     varchar(20)                                        not null,
    started_at timestamp with time zone default now()             not null,
    expired_at timestamp with time zone,
    created_at timestamp with time zone default now()             not null,
    updated_at timestamp with time zone default now()             not null
);

create table subscription_history
(
    id              bigserial
        constraint subscription_histories_pkey
            primary key,
    subscription_id uuid                                   not null
        constraint fk_history_subscription
            references subscriptions,
    prev_plan_id    bigint
        constraint fk_history_prev_plan
            references plans,
    next_plan_id    bigint                                 not null
        constraint fk_history_next_plan
            references plans,
    change_reason   varchar(100),
    created_at      timestamp with time zone default now() not null
);


-- 경험 관련
create table experiences
(
    id                 uuid        default gen_random_uuid()          not null
        primary key,
    user_id            uuid                                           not null
        constraint experiences_app_user_id_fk
            references app_user,
    title              text                                           not null,
    background         varchar(100),
    period_start       varchar(7)                                     not null,
    period_end         varchar(7),
    key_achievements   text,
    status             varchar(20) default 'DRAFT'::character varying not null,
    created_at         timestamp   default now()                      not null,
    updated_at         timestamp   default now()                      not null,
    skills             text,
    role               varchar(255),
    category           varchar(20),
    contribution_level varchar(20),
    goal_summary       text,
    progress_score     integer
);

comment on column experiences.title is '제목';

comment on column experiences.background is '배경 상황';

comment on column experiences.period_start is '기간 시작';

comment on column experiences.period_end is '기간 종료';

comment on column experiences.key_achievements is '핵심 성과 요약';

comment on column experiences.status is '상태';

comment on column experiences.skills is '기술 목록';

comment on column experiences.role is '역할';

comment on column experiences.category is '일의 유형';

comment on column experiences.contribution_level is '기여 수준';

comment on column experiences.goal_summary is '목표 요약';

create table experience_sections
(
    id            uuid        default gen_random_uuid()         not null
        primary key,
    experience_id uuid                                          not null
        references experiences
            on delete cascade,
    kind          varchar(20) default 'NONE'::character varying not null,
    title         varchar(255)                                  not null,
    content       text                                          not null,
    sort_order    integer     default 0                         not null,
    created_at    timestamp   default now()                     not null,
    updated_at    timestamp   default now()                     not null
);


-- AI 관련
create table ai_request
(
    id                uuid                                not null
        primary key,
    user_id           uuid                                not null,
    request_type      varchar(50)                         not null,
    status            varchar(20)                         not null,
    reference_id      uuid,
    reference_type    varchar(50),
    model_name        varchar(50),
    ai_provider_id    varchar(100),
    prompt_tokens     integer   default 0,
    completion_tokens integer   default 0,
    total_tokens      integer   default 0,
    raw_response      text,
    error_message     text,
    created_at        timestamp default CURRENT_TIMESTAMP not null,
    updated_at        timestamp default CURRENT_TIMESTAMP not null
);

create table ai_experience_analysis
(
    id                           uuid                                not null
        primary key,
    request_id                   uuid                                not null
        references ai_request,
    experience_id                uuid                                not null,
    overall_summary              text,
    overall_feedback             text,
    goal_feedback                text,
    goal_improved_content        text,
    achievement_feedback         text,
    achievement_improved_content text,
    recommended_keywords         text,
    created_at                   timestamp default CURRENT_TIMESTAMP not null,
    total_score                  integer,
    score_metrics                jsonb
);

create table ai_experience_section_analysis
(
    id               uuid                                not null
        primary key,
    analysis_id      uuid                                not null
        references ai_experience_analysis,
    section_id       uuid                                not null,
    method           varchar(20),
    feedback         text,
    improved_content text,
    reasoning        text,
    created_at       timestamp default CURRENT_TIMESTAMP not null,
    suggested_kind   varchar(30),
    method_breakdown jsonb
);

-- 알림
create table notifications
(
    id         uuid      default gen_random_uuid() not null
        primary key,
    user_id    uuid                                not null
        constraint fk_notifications_receiver
            references app_user
            on delete cascade,
    type       varchar(50)                         not null,
    title      varchar(255)                        not null,
    content    text                                not null,
    url        varchar(255),
    is_read    boolean   default false             not null,
    read_at    timestamp,
    created_at timestamp default now()             not null
);

-- 태그
create table tags
(
    id         uuid      default gen_random_uuid() not null
        primary key,
    user_id    uuid,
    name       varchar(100)                        not null,
    kind       varchar(20)                         not null,
    created_at timestamp default now()             not null
);


-- 로그
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
