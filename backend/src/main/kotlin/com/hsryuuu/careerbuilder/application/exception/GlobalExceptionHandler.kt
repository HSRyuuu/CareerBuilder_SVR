package com.hsryuuu.careerbuilder.application.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

/**
 * StandardResponse를 쓰지 않고 RFC7807 기반 ProblemDetail로
 * 에러 응답을 통일하는 핸들러 예시입니다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)
    /**
     * 서비스 정책 예외를 ProblemDetail로 변환합니다.
     */
    @ExceptionHandler(GlobalException::class)
    fun handleGlobalException(
        ex: GlobalException,
        request: HttpServletRequest
    ): ProblemDetail {
        val ec = ex.errorCode
        log.error("Error: {}", ec.message, ex)

        return ProblemDetail.forStatusAndDetail(ec.status, ec.message).apply {
            // type은 팀/프로젝트 규칙에 맞춰 URI 형태로 정의하는 것이 일반적입니다.
            type = URI.create("https://careerbuilder.example.com/problems/${ec.code.lowercase()}")
            title = ec.code
            instance = URI.create(request.requestURI)

            // 표준 필드 외 확장 필드도 추가할 수 있습니다.
            setProperty("code", ec.code)
            if (ex.data != null) {
                setProperty("data", ex.data)
            }
        }
    }

    /**
     * Bean Validation 결과를 ProblemDetail로 변환합니다.
     * - 여기서 400이 명확히 보장됩니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ProblemDetail {


        val errors = ex.bindingResult.fieldErrors.map {
            mapOf(
                "field" to it.field,
                "message" to (it.defaultMessage ?: "Invalid value")
            )
        }
        log.error("Error: {}",errors , ex)

        return ProblemDetail.forStatusAndDetail(
            ErrorCode.INVALID_REQUEST.status,
            ErrorCode.INVALID_REQUEST.message
        ).apply {
            type = URI.create("https://careerbuilder.example.com/problems/validation-error")
            title = "Validation failed"
            instance = URI.create(request.requestURI)

            setProperty("code", ErrorCode.INVALID_REQUEST.code)
            setProperty("errors", errors)
        }
    }

    /**
     * 마지막 안전망입니다.
     */
    @ExceptionHandler(Exception::class)
    fun handleUnknownException(
        ex: Exception,
        request: HttpServletRequest
    ): ProblemDetail {
        log.error("Error: {}", ex.message, ex)
        return ProblemDetail.forStatusAndDetail(
            org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 오류가 발생했습니다."
        ).apply {
            type = URI.create("https://careerbuilder.example.com/problems/internal-server-error")
            title = "INTERNAL_SERVER_ERROR"
            instance = URI.create(request.requestURI)
        }
    }
}