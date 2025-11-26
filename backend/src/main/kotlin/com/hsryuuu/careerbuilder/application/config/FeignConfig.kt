package com.hsryuuu.careerbuilder.application.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackages = ["com.hsryuuu.careerbuilder.infra.stockapi.api"])
class FeignConfig {
}