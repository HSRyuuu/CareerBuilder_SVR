package com.hsryuuu.careerbuilder.domain.user.auth.model

import com.hsryuuu.careerbuilder.application.security.UserInfo

data class LoginRequest(
    val username: String,
    val password: String
)


data class LoginResponse(
    val accessToken: String,
    val userInfo: UserInfo
)

data class LogoutResponse(
    val success: Boolean
)