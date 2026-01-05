package com.hsryuuu.careerbuilder.domain.ai.model.entity

import com.hsryuuu.careerbuilder.domain.ai.model.type.AiProcessType
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.model.type.ReferenceType
import jakarta.persistence.*
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "ai_request")
class AiRequest(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val requestType: AiProcessType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: AiRequestStatus = AiRequestStatus.PENDING,

    @Column(columnDefinition = "uuid")
    val referenceId: UUID? = null,

    @Column(length = 50)
    val referenceType: ReferenceType? = null,

    @Column(length = 50)
    var modelName: String? = null,

    @Column(length = 100)
    var aiProviderId: String? = null,

    var promptTokens: Int = 0,
    var completionTokens: Int = 0,
    var totalTokens: Int = 0,

    @Column(columnDefinition = "TEXT")
    var rawResponse: String? = null,

    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    fun updateByAiChatResponse(aiAnalysisId: UUID, chatResponse: ChatResponse) {
        val unknown = "unknown";
        val usage = chatResponse.metadata.usage
        this.aiProviderId = chatResponse.metadata.id ?: unknown
        this.modelName = chatResponse.metadata.model ?: unknown
        this.promptTokens = usage.promptTokens.toInt()
        this.completionTokens = usage.generationTokens.toInt()
        this.totalTokens = usage.totalTokens.toInt()
        this.rawResponse = "Saved to DB (ID: ${aiAnalysisId})"
    }

    fun fail(errorMessage: String) {
        this.status = AiRequestStatus.FAILURE
        this.errorMessage = errorMessage
    }
}