package com.hsryuuu.careerbuilder.domain.notification.controller

import com.hsryuuu.careerbuilder.application.annotation.CurrentUserId
import com.hsryuuu.careerbuilder.common.dto.CommonPageResponse
import com.hsryuuu.careerbuilder.domain.notification.model.dto.CreateNotificationRequest
import com.hsryuuu.careerbuilder.domain.notification.model.dto.NotificationResponse
import com.hsryuuu.careerbuilder.domain.notification.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @Operation(summary = "알림 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createNotification(@RequestBody request: CreateNotificationRequest) {
        notificationService.create(request)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getNotifications(
        @CurrentUserId userId: UUID,
        @RequestParam(required = false) isRead: Boolean?,
        @RequestParam(required = false) page: Int = 1,
        @RequestParam(required = false) pageSize: Int = 10,
    ): CommonPageResponse<NotificationResponse> {
        return notificationService.find(userId, isRead, page - 1, pageSize)
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun readNotification(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID
    ) {
        notificationService.read(id, userId)
    }
}
