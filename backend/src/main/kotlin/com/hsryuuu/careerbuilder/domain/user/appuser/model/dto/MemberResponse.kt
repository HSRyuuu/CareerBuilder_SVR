package com.hsryuuu.careerbuilder.domain.user.appuser.model.dto

import java.util.*

data class DuplicateCheckResponse(
    val exists: Boolean
)

data class SignupResponse(
    val id: UUID,
    val username: String,
    val nickname: String,
    val email: String
)
