package com.hsryuuu.careerbuilder.domain.user.auth

import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionHistoryRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserSignUpRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.generator.TestDataGenerator
import com.hsryuuu.careerbuilder.generator.TestDataGenerator.generateTestEmail
import com.hsryuuu.careerbuilder.generator.TestDataGenerator.generateTestPassword
import com.hsryuuu.careerbuilder.generator.TestDataGenerator.generateTestUsername
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("인증/인가 API TEST")
class AuthControllerTest {

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var subscriptionRepository: SubscriptionRepository

    @Autowired
    private lateinit var subscriptionHistoryRepository: SubscriptionHistoryRepository

    @Autowired
    private lateinit var client: TestRestTemplate

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @AfterEach
    fun deleteUser() {
        for (user in appUserRepository.findByUsernameContains(TestDataGenerator.TEST_USERNAME_PREFIX)) {
            val subscription = subscriptionRepository.findByUserId(user.id!!)
            print(subscription)
            val subscriptionHistories = subscriptionHistoryRepository.findBySubscriptionId(subscription!!.id!!)
            print(subscriptionHistories)
            subscriptionHistoryRepository.deleteAll(subscriptionHistories)
            subscriptionRepository.delete(subscription)
            appUserRepository.delete(user)
        }
        for (user in appUserRepository.findByEmailContains(TestDataGenerator.TEST_EMAIL_SUFFIX)) {
            val subscription = subscriptionRepository.findByUserId(user.id!!)
            val subscriptionHistories = subscriptionHistoryRepository.findBySubscriptionId(subscription!!.id!!)
            subscriptionHistoryRepository.deleteAll(subscriptionHistories)
            subscriptionRepository.delete(subscription)
            appUserRepository.delete(user)
        }
    }

    @Nested
    @DisplayName("회원가입 - /api/auth/signup")
    inner class Signup {

        @Test
        fun `올바르게 요청시 204 No Content를 반환`() {
            // Arrange
            val request = UserSignUpRequest(
                email = generateTestEmail(),
                username = generateTestUsername(),
                password = "test-password",
            )
            // Act
            val response = client.postForEntity("/api/auth/signup", request, Void::class.java)
            // Assert
            assertThat(response.statusCode.value()).isEqualTo(204)
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "invalid-email",
                "invalid-email@",
                "invalid-email@test",
                "invalid-email@test.",
                "invalid-email@.com"
            ]
        )
        fun `email 형식이 올바르지 않을 경우 400 Bad Request 상태 응답`(email: String) {
            // Arrange
            val request = UserSignUpRequest(
                email,
                username = generateTestUsername(),
                password = "test-password",
            )
            // Act
            val response = client.postForEntity("/api/auth/signup", request, Void::class.java)
            // Assert
            assertThat(response.statusCode.value()).isEqualTo(400)
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "",
                "te",
                "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"// 아이디는 4-20자
            ]
        )
        fun `username 형식이 올바르지 않을 경우 400 Bad Request 상태 응답`(username: String) {
            // Arrange
            val request = UserSignUpRequest(
                email = generateTestEmail(),
                username = username,
                password = "test-password",
            )
            // Act
            val response = client.postForEntity("/api/auth/signup", request, Void::class.java)
            // Assert
            assertThat(response.statusCode.value()).isEqualTo(400)
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "test-username",
                "ABCDEFG",
                "test-user-",
                "test."
            ]
        )
        fun `username 형식이 올바른 형식일 경우 204 No Content 상태 응답`(username: String) {
            // Arrange
            val request = UserSignUpRequest(
                email = generateTestEmail(),
                password = "test-password",
                username
            )
            // Act
            val response = client.postForEntity("/api/auth/signup", request, Void::class.java)
            // Assert
            assertThat(response.statusCode.value()).isEqualTo(204)
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "",
                "short", // 8자 미만
            ]
        )
        fun `password 형식이 올바르지 않을 경우 400 Bad Request 상태 응답`(password: String) {
            // Arrange
            val request = UserSignUpRequest(
                email = generateTestEmail(),
                username = generateTestUsername(),
                password = password,
            )
            // Act
            val response = client.postForEntity("/api/auth/signup", request, Void::class.java)
            // Assert
            assertThat(response.statusCode.value()).isEqualTo(400)
        }

        @Test
        fun `email이 중복일 경우 409 Conflict 상태 응답`() {
            // Arrange
            val email = generateTestEmail()
            client.postForEntity(
                "/api/auth/signup",
                UserSignUpRequest(
                    email = email,
                    username = generateTestUsername(),
                    password = "test-password",
                ),
                Void::class.java
            )
            // Act
            val response = client.postForEntity(
                "/api/auth/signup",
                UserSignUpRequest(
                    email = email,
                    password = "test-password",
                    username = generateTestUsername(),
                ),
                Void::class.java
            )
            // Assert
            assertThat(response.statusCode.value()).isEqualTo(409)
        }

        @Test
        fun `username이 중복일 경우 409 Conflict 상태 응답`() {
            // Arrange
            val username = generateTestUsername()
            client.postForEntity(
                "/api/auth/signup",
                UserSignUpRequest(
                    email = generateTestEmail(),
                    username = username,
                    password = "test-password",
                ),
                Void::class.java
            )
            // Act
            val response = client.postForEntity(
                "/api/auth/signup",
                UserSignUpRequest(
                    email = generateTestEmail(),
                    password = "test-password",
                    username,
                ),
                Void::class.java
            )
            // Assert
            assertThat(response.statusCode.value()).isEqualTo(409)
        }

        @Test
        fun `password를 올바르게 암호화 한다`() {
            // Arrange
            val request = UserSignUpRequest(
                email = generateTestEmail(),
                username = generateTestUsername(),
                password = generateTestPassword(),
            )
            // Act
            client.postForEntity("/api/auth/signup", request, Void::class.java)
            // Assert
            val appUser = appUserRepository.findByEmail(request.email)
                ?: throw IllegalArgumentException("no-data")
            val actual = appUser.password
            assertThat(actual).isNotNull
            assertThat(passwordEncoder.matches(request.password, actual)).isTrue
        }
    }
}
