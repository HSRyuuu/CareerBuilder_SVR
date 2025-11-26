package com.hsryuuu.careerbuilder.domain.user.auth

import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.DuplicateCheckResponse
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.SignupResponse
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserSignupRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.service.AuthService
import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginRequest
import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginResponse
import com.hsryuuu.careerbuilder.domain.user.auth.model.LogoutResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "인증/인가 API")
@RequestMapping("/api/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {
    @GetMapping("/check/username")
    fun checkUsernameExists(@RequestParam username: String): DuplicateCheckResponse {
        val exists = authService.existsByUsername(username)
        return DuplicateCheckResponse(exists)
    }

    @GetMapping("/check/nickname")
    fun checkNicknameExists(@RequestParam nickname: String): DuplicateCheckResponse {
        val exists = authService.existsByNickname(nickname)
        return DuplicateCheckResponse(exists)
    }

    @GetMapping("/check/email")
    fun checkEmailExists(@RequestParam email: String): DuplicateCheckResponse {
        val exists = authService.existsByEmail(email)
        return DuplicateCheckResponse(exists)
    }

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: UserSignupRequest): SignupResponse {
        val userId = authService.signup(request)
        return SignupResponse(
            id = userId,
            username = request.username,
            nickname = request.nickname,
            email = request.email
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return authService.login(request)
    }

    @GetMapping("/logout")
    fun logout(httpServletRequest: HttpServletRequest): LogoutResponse {
        // logout
        authService.logout(httpServletRequest)
        return LogoutResponse(true)
    }
}