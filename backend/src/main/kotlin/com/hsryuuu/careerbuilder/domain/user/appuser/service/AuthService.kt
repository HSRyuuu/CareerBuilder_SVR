package com.hsryuuu.careerbuilder.domain.user.appuser.service

import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.application.security.AuthManager
import com.hsryuuu.careerbuilder.application.security.JwtTokenProvider
import com.hsryuuu.careerbuilder.application.security.UserInfo
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserSignupRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository

import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginRequest
import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuthService(
    private val userRepository: AppUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authManager: AuthManager
) {

    fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    fun existsByNickname(nickname: String): Boolean {
        return userRepository.existsByNickname(nickname)
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    @Transactional
    fun signup(request: UserSignupRequest): UUID {
        // 중복 체크
        if (existsByUsername(request.username)) {
            throw IllegalArgumentException("이미 사용중인 아이디입니다.")
        }
        if (existsByNickname(request.nickname)) {
            throw IllegalArgumentException("이미 사용중인 닉네임입니다.")
        }

        if (existsByEmail(request.email)) {
            throw IllegalArgumentException("이미 사용중인 이메일입니다.")
        }

        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(request.password)

        // 회원 저장
        val appUser = AppUser(
            username = request.username,
            password = encodedPassword,
            nickname = request.nickname,
            email = request.email
        )

        val savedUser = userRepository.save(appUser)
        return savedUser.id ?: throw IllegalStateException("회원 저장에 실패했습니다.")
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw GlobalException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다.")
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw GlobalException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.")
        }
        val userInfo = UserInfo.from(user)
        return LoginResponse(jwtTokenProvider.createToken(userInfo), userInfo)
    }

    fun logout(httpServletRequest: HttpServletRequest) {
        val accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)
        val userInfo = authManager.getCurrentUser()
        // TODO: accessToken 블랙리스트
    }


}