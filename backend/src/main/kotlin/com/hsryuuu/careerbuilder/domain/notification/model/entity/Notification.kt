package com.hsryuuu.careerbuilder.domain.notification.model.entity

import com.hsryuuu.careerbuilder.domain.notification.model.enums.NotificationType
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @UuidGenerator
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(name = "url")
    val url: String? = null,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(name = "read_at")
    var readAt: LocalDateTime? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun read() {
        this.isRead = true
        this.readAt = LocalDateTime.now()
    }
}
