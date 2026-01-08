package com.hsryuuu.careerbuilder.domain.notification.repository

import com.hsryuuu.careerbuilder.domain.notification.model.entity.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findAllByUserId(userId: UUID, pageable: Pageable): Page<Notification>
    fun findAllByUserIdAndIsRead(userId: UUID, isRead: Boolean, pageable: Pageable): Page<Notification>
    fun findAllByUserIdAndIsRead(userId: UUID, isRead: Boolean): List<Notification>
}