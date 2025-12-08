package com.hsryuuu.careerbuilder.domain.user.appuser.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.*

data class UserSignUpRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "올바른 이메일 형식이 아닙니다."
    )
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    val password: String,

    @field:NotBlank(message = "아이디는 필수입니다.")
    @field:Size(min = 4, max = 50, message = "아이디는 4~20자 사이여야 합니다.")
    val username: String,
)

data class DuplicateCheckResponse(
    val exists: Boolean
)

data class SignupResponse(
    val id: UUID,
    val username: String,
    val email: String
)

