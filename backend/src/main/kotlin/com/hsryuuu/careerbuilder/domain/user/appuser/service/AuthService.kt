package com.hsryuuu.careerbuilder.domain.user.appuser.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.application.security.JwtTokenProvider
import com.hsryuuu.careerbuilder.application.security.UserInfo
import com.hsryuuu.careerbuilder.domain.plan.model.entity.Subscription
import com.hsryuuu.careerbuilder.domain.plan.model.entity.SubscriptionHistory
import com.hsryuuu.careerbuilder.domain.plan.model.entity.SubscriptionStatus
import com.hsryuuu.careerbuilder.domain.plan.model.type.PlanType
import com.hsryuuu.careerbuilder.domain.plan.repository.PlanRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionHistoryRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserSignUpRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginRequest
import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginResponse
import com.hsryuuu.careerbuilder.domain.user.auth.model.LogoutResponse
import com.hsryuuu.careerbuilder.domain.user.auth.model.RefreshResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class AuthService(
    private val userRepository: AppUserRepository,
    private val planRepository: PlanRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionHistoryRepository: SubscriptionHistoryRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val redisTemplate: StringRedisTemplate,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    @Transactional
    fun signup(request: UserSignUpRequest): UUID {
        // 중복 체크
        if (existsByEmail(request.email)) {
            throw GlobalException(ErrorCode.DUPLICATE_EMAIL)
        }
        if (existsByUsername(request.username)) {
            throw GlobalException(ErrorCode.DUPLICATE_USERNAME)
        }

        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(request.password)

        // 회원 저장
        val appUser = AppUser(
            username = request.username,
            password = encodedPassword,
            email = request.email
        )
        val savedUser = userRepository.save(appUser)
        savePlanSubscription(savedUser)
        return savedUser.id!!
    }

    private fun savePlanSubscription(user: AppUser) {
        val plan = planRepository.findByPlanType(PlanType.BASIC) ?: throw GlobalException(ErrorCode.PLAN_NOT_FOUND)
        val subscription = subscriptionRepository.save(
            Subscription(
                user = user,
                plan = plan,
                status = SubscriptionStatus.ACTIVE
            )
        )
        subscriptionHistoryRepository.save(
            SubscriptionHistory(
                subscription = subscription,
                nextPlan = plan,
                changeReason = "회원가입"
            )
        )

    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw GlobalException(ErrorCode.USER_NOT_FOUND)
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw GlobalException(ErrorCode.INVALID_PASSWORD)
        }
        val userInfo = UserInfo.from(user)
        val accessToken = jwtTokenProvider.createToken(userInfo)
        val refreshToken = jwtTokenProvider.createRefreshToken(userInfo.username)
        return LoginResponse(accessToken, refreshToken, userInfo)
    }

    fun logout(httpServletRequest: HttpServletRequest): LogoutResponse {
        val authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)
        var accessToken: String? = null
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7)
        }
        try {
            redisTemplate.opsForValue().set(
                "blacklist:$accessToken",
                "logout",
                jwtTokenProvider.validityInMilliseconds,
                TimeUnit.MILLISECONDS
            )
            return LogoutResponse(true)
        } catch (e: Exception) {
            log.error("AuthService Logout - Redis Error", e)
        }
        return LogoutResponse(false)
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token 발급
     */
    @Transactional(readOnly = true)
    fun refresh(refreshToken: String): RefreshResponse {
        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw GlobalException(ErrorCode.UNAUTHORIZED)
        }

        // Refresh Token에서 username 추출
        val username = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken)

        // 사용자 조회
        val user = userRepository.findByUsername(username)
            ?: throw GlobalException(ErrorCode.USER_NOT_FOUND)

        // 새로운 토큰 발급
        val userInfo = UserInfo.from(user)
        val newAccessToken = jwtTokenProvider.createToken(userInfo)
        val newRefreshToken = jwtTokenProvider.createRefreshToken(username)

        return RefreshResponse(newAccessToken, newRefreshToken)
    }

}