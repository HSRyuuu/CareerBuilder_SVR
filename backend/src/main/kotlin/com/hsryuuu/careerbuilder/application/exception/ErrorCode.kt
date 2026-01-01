package com.hsryuuu.careerbuilder.application.exception

import org.springframework.http.HttpStatus

/**
 * 서비스 정책을 식별하기 위한 에러 코드 정의입니다.
 */
enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    // 공통
    FORBIDDEN(HttpStatus.FORBIDDEN, "리소스에 접근할 수 없습니다."),
    BAD_ENUM_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    DUPLICATE_VALUE(HttpStatus.CONFLICT, "이미 존재하는 값이 있습니다."),

    // Auth
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 사용자명입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),

    // MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),


    // Experience
    EXPERIENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "경험 항목을 찾을 수 없습니다."),
    VALIDATION_ERROR_DURATION_SEQUENCE(
        HttpStatus.BAD_REQUEST, "시작일은 종료일보다 빨라야합니다."
    ),
    EXPERIENCE_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "제목은 필수값입니다.(5자 이상)"),
    EXPERIENCE_BACKGROUND_REQUIRED(HttpStatus.BAD_REQUEST, "배경/소속/단체 를 입력해주세요."),
    EXPERIENCE_ROLE_REQUIRED(HttpStatus.BAD_REQUEST, "주요 역할을 입력해주세요."),
    EXPERIENCE_INCOMPLETE(HttpStatus.BAD_REQUEST, "경험 작성에 보완이 필요합니다. 필수값을 완성하고, 목표와 핵심 성과를 20자 이상 입력해주세요. "),

    // AI
    AI_EXPERIENCE_ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "AI 경험 분석 결과가 존재하지 않습니다."),
    AI_EXPERIENCE_ANALYSIS_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "AI 경험 분석 결과를 아직 확인하지 않았습니다."),
}