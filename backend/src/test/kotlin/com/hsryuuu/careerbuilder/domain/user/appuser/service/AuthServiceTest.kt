package com.hsryuuu.careerbuilder.domain.user.appuser.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.application.security.JwtTokenProvider
import com.hsryuuu.careerbuilder.domain.plan.model.entity.Plan
import com.hsryuuu.careerbuilder.domain.plan.model.entity.PlanType
import com.hsryuuu.careerbuilder.domain.plan.repository.PlanRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionHistoryRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import com.hsryuuu.careerbuilder.application.security.UserInfo
import com.hsryuuu.careerbuilder.domain.plan.model.entity.Subscription
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserSignUpRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.domain.user.auth.model.LoginRequest
import jakarta.servlet.http.HttpServletRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
@Transactional
@DisplayName("인증 서비스 테스트")
class AuthServiceTest {

    @Autowired
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var userRepository: AppUserRepository

    @MockBean
    private lateinit var planRepository: PlanRepository

    @MockBean
    private lateinit var subscriptionRepository: SubscriptionRepository

    @MockBean
    private lateinit var subscriptionHistoryRepository: SubscriptionHistoryRepository

    @MockBean
    private lateinit var passwordEncoder: PasswordEncoder

    @MockBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    private lateinit var authManager: com.hsryuuu.careerbuilder.application.security.AuthManager

    @MockBean
    private lateinit var redisTemplate: StringRedisTemplate

    private lateinit var testUser: AppUser

    @BeforeEach
    fun setUp() {
        testUser = AppUser(
            id = UUID.randomUUID(),
            username = "testuser",
            email = "test@example.com",
            password = "encodedPassword"
        )
        given(jwtTokenProvider.validityInMilliseconds).willReturn(3600000L)
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    fun signup_Success() {
        // given
        val request = UserSignUpRequest("testuser", "test@example.com", "password")
        val plan = Plan(
            id = 1L,
            name = "Basic Plan",
            planType = PlanType.BASIC,
            experienceAnalysisModel = "gpt-4o",
            experienceAnalysisLimitPerDay = 10,
            careerAnalysisModel = "gpt-4o",
            careerAnalysisLimitPerMonth = 5,
            resumeModel = "gpt-4o",
            resumeLimitPerMonth = 3
        )

        given(userRepository.existsByEmail(request.email)).willReturn(false)
        given(userRepository.existsByUsername(request.username)).willReturn(false)
        given(passwordEncoder.encode(request.password)).willReturn("encodedPassword")
        given(userRepository.save(any())).willReturn(testUser)
        given(planRepository.findByPlanType(PlanType.BASIC)).willReturn(plan)
        given(subscriptionRepository.save(any())).willReturn(
            Subscription(
                id = UUID.randomUUID(),
                user = testUser,
                plan = plan
            )
        )

        // when
        val userId = authService.signup(request)

        // then
        assertThat(userId).isEqualTo(testUser.id)
        verify(userRepository).save(any())
        verify(subscriptionRepository).save(any())
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 가입 시 예외가 발생한다")
    fun signup_DuplicateEmail() {
        // given
        val request = UserSignUpRequest("testuser", "test@example.com", "password")
        given(userRepository.existsByEmail(request.email)).willReturn(true)

        // when & then
        assertThatThrownBy { authService.signup(request) }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL)
    }

    @Test
    @DisplayName("로그인에 성공한다")
    fun login_Success() {
        // given
        val request = LoginRequest("testuser", "password")
        
        given(userRepository.findByUsername(request.username)).willReturn(testUser)
        given(passwordEncoder.matches(request.password, testUser.password)).willReturn(true)
        given(jwtTokenProvider.createToken(any(UserInfo::class.java))).willReturn("testToken")

        // when
        val response = authService.login(request)

        // then
        assertThat(response.accessToken).isEqualTo("testToken")
        assertThat(response.userInfo.username).isEqualTo(testUser.username)
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
    fun login_InvalidPassword() {
        // given
        val request = LoginRequest("testuser", "wrongPassword")
        given(userRepository.findByUsername(request.username)).willReturn(testUser)
        given(passwordEncoder.matches(request.password, testUser.password)).willReturn(false)

        // when & then
        assertThatThrownBy { authService.login(request) }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD)
    }

    @Test
    @DisplayName("로그아웃 시 토큰이 블랙리스트에 등록된다")
    fun logout_Success() {
        // given
        val request = mock(HttpServletRequest::class.java)
        val token = "testToken"
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer $token")
        given(jwtTokenProvider.validityInMilliseconds).willReturn(3600000L)
        
        val valueOperations = mock(ValueOperations::class.java) as ValueOperations<String, String>
        given(redisTemplate.opsForValue()).willReturn(valueOperations)

        // when
        authService.logout(request)

        // then
        verify(valueOperations).set(
            eq("blacklist:$token"),
            eq("logout"),
            eq(3600000L),
            eq(TimeUnit.MILLISECONDS)
        )
    }
}
