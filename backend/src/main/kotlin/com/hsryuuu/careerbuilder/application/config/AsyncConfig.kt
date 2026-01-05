package com.hsryuuu.careerbuilder.application.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig {

    @Bean(name = ["aiExecutor"])
    fun aiExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.maxPoolSize = 20
        executor.queueCapacity = 100
        executor.setThreadNamePrefix("AiExecutor-")
        executor.initialize()
        return executor
    }

    // 알림용: I/O (DB, Network) 위주의 작업
    @Bean(name = ["notificationExecutor"])
    fun notificationExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 3
        executor.maxPoolSize = 10
        executor.queueCapacity = 500 // 알림은 좀 더 넉넉하게 대기열 설정 가능
        executor.setThreadNamePrefix("NotiExecutor-")
        executor.initialize()
        return executor
    }
}
