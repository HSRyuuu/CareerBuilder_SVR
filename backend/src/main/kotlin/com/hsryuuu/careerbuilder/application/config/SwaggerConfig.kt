package com.hsryuuu.careerbuilder.application.config

import com.hsryuuu.careerbuilder.application.annotation.CurrentUserId
import com.hsryuuu.careerbuilder.application.security.JwtTokenProvider
import com.hsryuuu.careerbuilder.application.security.UserInfo
import com.hsryuuu.careerbuilder.application.security.UserRole
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class SwaggerConfig {

    init {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(CurrentUserId::class.java)
    }

    @Bean
    fun realtimeStockTrackerOpenApi(): OpenAPI {
        val securitySchemeName = "bearerAuth"

        return OpenAPI()
            .info(
                Info()
                    .title("Career Builder")
                    .description("Career Builder API")
                    .version("v1.0.0")
            )
            // 보안 스키마 정의
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name("Authorization")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description(jwtToken())
                    )
            )
            // 모든 API 요청에 인증 필요
            .addSecurityItem(
                SecurityRequirement().addList(securitySchemeName)
            )

    }

    fun jwtToken(): String {
        val userInfo = UserInfo(
            id = UUID.fromString("e5185f5b-a92b-4dd1-a8a2-4319a4244aa5"),
            username = "hsryuuu",
            email = "happyhsryu@gmail.com",
            role = UserRole.ROLE_USER
        )
        return JwtTokenProvider().createToken(userInfo)
    }
}