package com.hsryuuu.careerbuilder.application.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth.jwt")
data class JwtProperties(
    val secret: String,
    val expiration: Long // milliseconds
)