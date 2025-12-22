package com.hsryuuu.careerbuilder.domain.log.service

import com.hsryuuu.careerbuilder.domain.log.model.dto.CreateUserActionLogRequest
import com.hsryuuu.careerbuilder.domain.log.model.dto.UserActionLogResponse
import com.hsryuuu.careerbuilder.domain.log.repository.UserActionLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserActionLogService(
    private val userActionLogRepository: UserActionLogRepository
) {

    @Transactional
    fun createLog(
        request: CreateUserActionLogRequest,
        userAgent: String?,
        ipAddress: String?
    ): UserActionLogResponse {
        val log = request.toEntity(userAgent, ipAddress)
        val saved = userActionLogRepository.save(log)
        return UserActionLogResponse.from(saved)
    }
}
