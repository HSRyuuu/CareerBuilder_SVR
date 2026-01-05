package com.hsryuuu.careerbuilder.domain.notification.event

import com.hsryuuu.careerbuilder.domain.notification.model.entity.Notification
import com.hsryuuu.careerbuilder.domain.notification.repository.NotificationRepository
import com.hsryuuu.careerbuilder.domain.notification.utils.NotificationUrlProvider
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationEventListener(
    private val notificationRepository: NotificationRepository,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Async("notificationExecutor") // 명시적 이름 지정
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleNotificationEvent(event: NotificationEvent) {
        log.info("Received notification {}, userId={}, targetId={}", event.type.name, event.userId, event.targetId)
        val notification = notificationRepository.save(
            Notification(
                userId = event.userId,
                type = event.type,
                title = event.type.title,
                content = event.type.message,
                url = NotificationUrlProvider.createUrl(event.type, event.targetId),
            )
        )
        log.info("saved notification {}", notification.id)

    }
}