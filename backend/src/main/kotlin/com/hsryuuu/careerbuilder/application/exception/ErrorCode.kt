package com.hsryuuu.careerbuilder.application.exception

import org.springframework.http.HttpStatus

/**
 * 서비스 정책을 식별하기 위한 에러 코드 정의입니다.
 */
enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // 공통
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "리소스에 접근할 수 없습니다."),
    BAD_ENUM_REQUEST(HttpStatus.BAD_REQUEST, "BAD_ENUM_REQUEST", "잘못된 요청값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."),
    DUPLICATE_VALUE(HttpStatus.CONFLICT, "DUPLICATE_VALUE", "이미 존재하는 값이 있습니다."),

    // Auth
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "DUPLICATE_USERNAME", "이미 존재하는 username입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 존재하는 email입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청 값이 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),

    // MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다."),


    // Achievement
    ACHIEVEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACHIEVEMENT_NOT_FOUND", "성과 항목을 찾을 수 없습니다."),
    VALIDATION_ERROR_DURATION_SEQUENCE(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR_DURATION_SEQUENCE", "기간 순서가 잘못되었습니다.")
}