package com.hsryuuu.careerbuilder.domain.user.appuser.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.application.security.AuthManager
import com.hsryuuu.careerbuilder.application.security.JwtTokenProvider
import com.hsryuuu.careerbuilder.application.security.UserInfo
import com.hsryuuu.careerbuilder.domain.plan.model.entity.PlanType
import com.hsryuuu.careerbuilder.domain.plan.model.entity.Subscription
import com.hsryuuu.careerbuilder.domain.plan.model.entity.SubscriptionHistory
import com.hsryuuu.careerbuilder.domain.plan.model.entity.SubscriptionStatus
import com.hsryuuu.careerbuilder.domain.plan.repository.PlanRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionHistoryRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserSignUpRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginRequest
import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginResponse
import com.hsryuuu.careerbuilder.domain.user.auth.model.LogoutResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
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
    private val authManager: AuthManager
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


        try {
            val savedUser = userRepository.save(appUser)
            // 기본 플랜 생성
            savePlanSubscription(savedUser)
            return savedUser.id!!
        } catch (e: DataIntegrityViolationException) {
            throw GlobalException(ErrorCode.DUPLICATE_VALUE)
        }
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
        return LoginResponse(jwtTokenProvider.createToken(userInfo), userInfo)
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


}