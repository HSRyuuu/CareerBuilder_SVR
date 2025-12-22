package com.hsryuuu.careerbuilder.domain.log

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(
    name = "user_action_log",
    indexes = [
        Index(name = "idx_user_action_log_event_name", columnList = "eventName"),
        Index(name = "idx_user_action_log_user_id", columnList = "userId"),
        Index(name = "idx_user_action_log_timestamp", columnList = "timestamp"),
        Index(name = "idx_user_action_log_session_id", columnList = "sessionId")
    ]
)
data class UserActionLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    val eventName: String,

    @Column(nullable = false, length = 50)
    val eventCategory: String,

    @Column(columnDefinition = "TEXT")
    val properties: String? = null, // JSON 형태의 이벤트 속성

    @Column(length = 100)
    val userId: String? = null,

    @Column(length = 100)
    val sessionId: String? = null,

    @Column(length = 500)
    val pageUrl: String? = null,

    @Column(length = 500)
    val referrer: String? = null,

    @Column(length = 500)
    val userAgent: String? = null,

    @Column(length = 50)
    val ipAddress: String? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)