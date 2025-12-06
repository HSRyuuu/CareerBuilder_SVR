package com.hsryuuu.careerbuilder.domain.user.auth

import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserSignUpRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.generator.EmailGenerator
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
        appUserRepository.deleteByUsername(TEST_USERNAME)
        appUserRepository.deleteByEmailLike(EmailGenerator.TEST_EMAIL_SUFFIX)
    }

    @Test
    fun signup_올바르게_요청시_204_No_Content를_반환(
        @Autowired client: TestRestTemplate
    ) {
        // Arrange
        val request = UserSignUpRequest(
            email = EmailGenerator.generateTestEmail(),
            username = TEST_USERNAME,
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
            username = TEST_USERNAME,
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
            "abcdefghijklmnopqrstuvwxyz"// 아이디는 4-20자
        ]
    )
    fun signup_username_형식이_올바르지_않을_경우_400_Bad_Request_상태_응답(
        username: String,
        @Autowired client: TestRestTemplate
    ) {
        // Arrange
        val request = UserSignUpRequest(
            email = EmailGenerator.generateTestEmail(),
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
            email = EmailGenerator.generateTestEmail(),
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
            email = EmailGenerator.generateTestEmail(),
            username = TEST_USERNAME,
            password = password,
        )
        // Act
        val response = client.postForEntity("/api/auth/signup", request, Void::class.java)
        // Assert
        assertThat(response.statusCode.value()).isEqualTo(400)
    }

    @Test
    fun signup_username_중복일_경우_400_Bad_Request_상태_응답(
        @Autowired client: TestRestTemplate
    ) {
        // Arrange
        val username = TEST_USERNAME
        client.postForEntity(
            "/api/auth/signup",
            UserSignUpRequest(
                email = EmailGenerator.generateTestEmail(),
                username = username,
                password = "test-password",
            ),
            Void::class.java
        )
        // Act
        val response = client.postForEntity(
            "/api/auth/signup",
            UserSignUpRequest(
                email = EmailGenerator.generateTestEmail(),
                password = "test-password",
                username,
            ),
            Void::class.java
        )
        // Assert
        assertThat(response.statusCode.value()).isEqualTo(409)

    }

}