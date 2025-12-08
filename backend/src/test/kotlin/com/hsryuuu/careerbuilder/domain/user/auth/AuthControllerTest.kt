package com.hsryuuu.careerbuilder.domain.user.auth

import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserSignUpRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.generator.TestDataGenerator
import com.hsryuuu.careerbuilder.generator.TestDataGenerator.generateTestEmail
import com.hsryuuu.careerbuilder.generator.TestDataGenerator.generateTestUsername
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("인증/인가 API TEST")
class AuthControllerTest(
    @Autowired private val appUserRepository: AppUserRepository,
) {

    companion object {
        private const val TEST_USERNAME = "test-user"
    }

    @AfterEach
    fun deleteUser() {
        appUserRepository.deleteByUsernameLike(TestDataGenerator.TEST_USERNAME_PREFIX)
        appUserRepository.deleteByEmailLike(TestDataGenerator.TEST_EMAIL_SUFFIX)
    }

    @Test
    fun signup_올바르게_요청시_204_No_Content를_반환(
        @Autowired client: TestRestTemplate
    ) {
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
    fun signup_email_형식이_올바르지_않을_경우_400_Bad_Request_상태_응답(
        email: String,
        @Autowired client: TestRestTemplate
    ) {
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
    fun signup_username_형식이_올바르지_않을_경우_400_Bad_Request_상태_응답(
        username: String,
        @Autowired client: TestRestTemplate
    ) {
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
    fun signup_username_형식이_올바른_형식일_경우_204_No_Content_상태_응답(
        username: String,
        @Autowired client: TestRestTemplate
    ) {
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
    fun signup_password_형식이_올바르지_않을_경우_400_Bad_Request_상태_응답(
        password: String,
        @Autowired client: TestRestTemplate
    ) {
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
    fun signup_email이_중복일_경우_409_Conflict_상태_응답(
        @Autowired client: TestRestTemplate
    ) {
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
    fun signup_username이_중복일_경우_409_Conflict_상태_응답(
        @Autowired client: TestRestTemplate
    ) {
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

}