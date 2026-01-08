package com.hsryuuu.careerbuilder.domain.notification.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.common.dto.CommonPageResponse
import com.hsryuuu.careerbuilder.domain.notification.model.dto.NotificationResponse
import com.hsryuuu.careerbuilder.domain.notification.repository.NotificationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
) {

    @Transactional(readOnly = true)
    fun find(
        userId: UUID,
        isRead: Boolean?,
        page: Int,
        pageSize: Int
    ): CommonPageResponse<NotificationResponse> {
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val pageRequest = PageRequest.of(page, pageSize, sort)

        val page = if (isRead == null) {
            notificationRepository.findAllByUserId(userId, pageRequest)
        } else {
            notificationRepository.findAllByUserIdAndIsRead(userId, isRead, pageRequest)
        }
        return CommonPageResponse.from(page) { NotificationResponse.from(it) }
    }

    @Transactional
    fun read(id: UUID, userId: UUID) {
        val notification = notificationRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Notification not found with id: $id") }
        if (notification.userId != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }
        notification.read()
    }

    @Transactional
    fun readAll(userId: UUID) {
        val notifications = notificationRepository.findAllByUserIdAndIsRead(userId, false)
        notifications.forEach { it.read() }
        notificationRepository.saveAll(notifications)
    }
}