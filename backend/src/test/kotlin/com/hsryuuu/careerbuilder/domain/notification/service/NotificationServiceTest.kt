package com.hsryuuu.careerbuilder.domain.notification.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.notification.model.entity.Notification
import com.hsryuuu.careerbuilder.domain.notification.model.enums.NotificationType
import com.hsryuuu.careerbuilder.domain.notification.repository.NotificationRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@DisplayName("알림 서비스 테스트")
class NotificationServiceTest {

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    private lateinit var notificationService: NotificationService

    private lateinit var testUserId: UUID
    private lateinit var otherUserId: UUID

    @BeforeEach
    fun setUp() {
        notificationService = NotificationService(notificationRepository)
        testUserId = UUID.randomUUID()
        otherUserId = UUID.randomUUID()
    }

    private fun createTestNotification(
        id: UUID = UUID.randomUUID(),
        userId: UUID = testUserId,
        type: NotificationType = NotificationType.AI_EXPERIENCE_ANALYSIS_SUCCESS,
        title: String = "테스트 알림",
        content: String = "테스트 알림 내용입니다.",
        url: String? = "/experience/123",
        isRead: Boolean = false,
        readAt: LocalDateTime? = null,
        createdAt: LocalDateTime = LocalDateTime.now()
    ) = Notification(
        id = id,
        userId = userId,
        type = type,
        title = title,
        content = content,
        url = url,
        isRead = isRead,
        readAt = readAt,
        createdAt = createdAt
    )

    @Nested
    @DisplayName("알림 목록 조회 - find()")
    inner class Find {

        @Test
        @DisplayName("[SUCCESS] 전체 알림 목록 조회 성공")
        fun 전체_알림_목록_조회_성공() {
            // Arrange
            val notifications = listOf(
                createTestNotification(title = "알림1"),
                createTestNotification(title = "알림2"),
                createTestNotification(title = "알림3")
            )
            val pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            val page = PageImpl(notifications, pageRequest, 3)

            whenever(notificationRepository.findAllByUserId(eq(testUserId), any()))
                .thenReturn(page)

            // Act
            val result = notificationService.find(
                userId = testUserId,
                isRead = null,
                page = 0,
                pageSize = 10
            )

            // Assert
            assertThat(result.total).isEqualTo(3)
            assertThat(result.list).hasSize(3)
            verify(notificationRepository).findAllByUserId(eq(testUserId), any())
            verify(notificationRepository, never()).findAllByUserIdAndIsRead(any(), any(), any<PageRequest>())
        }

        @Test
        @DisplayName("[SUCCESS] 읽지 않은 알림만 조회 성공")
        fun 읽지_않은_알림만_조회_성공() {
            // Arrange
            val unreadNotifications = listOf(
                createTestNotification(title = "읽지 않은 알림1", isRead = false),
                createTestNotification(title = "읽지 않은 알림2", isRead = false)
            )
            val pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            val page = PageImpl(unreadNotifications, pageRequest, 2)

            whenever(notificationRepository.findAllByUserIdAndIsRead(eq(testUserId), eq(false), any<PageRequest>()))
                .thenReturn(page)

            // Act
            val result = notificationService.find(
                userId = testUserId,
                isRead = false,
                page = 0,
                pageSize = 10
            )

            // Assert
            assertThat(result.total).isEqualTo(2)
            assertThat(result.list).hasSize(2)
            assertThat(result.list).allMatch { !it.isRead }
            verify(notificationRepository).findAllByUserIdAndIsRead(eq(testUserId), eq(false), any<PageRequest>())
            verify(notificationRepository, never()).findAllByUserId(any(), any())
        }

        @Test
        @DisplayName("[SUCCESS] 읽은 알림만 조회 성공")
        fun 읽은_알림만_조회_성공() {
            // Arrange
            val readNotifications = listOf(
                createTestNotification(title = "읽은 알림1", isRead = true, readAt = LocalDateTime.now())
            )
            val pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            val page = PageImpl(readNotifications, pageRequest, 1)

            whenever(notificationRepository.findAllByUserIdAndIsRead(eq(testUserId), eq(true), any<PageRequest>()))
                .thenReturn(page)

            // Act
            val result = notificationService.find(
                userId = testUserId,
                isRead = true,
                page = 0,
                pageSize = 10
            )

            // Assert
            assertThat(result.total).isEqualTo(1)
            assertThat(result.list).hasSize(1)
            assertThat(result.list).allMatch { it.isRead }
            verify(notificationRepository).findAllByUserIdAndIsRead(eq(testUserId), eq(true), any<PageRequest>())
        }

        @Test
        @DisplayName("[SUCCESS] 빈 목록 조회 성공")
        fun 빈_목록_조회_성공() {
            // Arrange
            val pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            val emptyPage = PageImpl<Notification>(emptyList(), pageRequest, 0)

            whenever(notificationRepository.findAllByUserId(eq(testUserId), any()))
                .thenReturn(emptyPage)

            // Act
            val result = notificationService.find(
                userId = testUserId,
                isRead = null,
                page = 0,
                pageSize = 10
            )

            // Assert
            assertThat(result.total).isEqualTo(0)
            assertThat(result.list).isEmpty()
        }

        @Test
        @DisplayName("[SUCCESS] 페이징 조회 성공")
        fun 페이징_조회_성공() {
            // Arrange
            val notifications = listOf(
                createTestNotification(title = "페이지2 알림1"),
                createTestNotification(title = "페이지2 알림2")
            )
            val pageRequest = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "createdAt"))
            val page = PageImpl(notifications, pageRequest, 10)

            whenever(notificationRepository.findAllByUserId(eq(testUserId), any()))
                .thenReturn(page)

            // Act
            val result = notificationService.find(
                userId = testUserId,
                isRead = null,
                page = 1,
                pageSize = 2
            )

            // Assert
            assertThat(result.currentPage).isEqualTo(1)
            assertThat(result.pageSize).isEqualTo(2)
            assertThat(result.total).isEqualTo(10)
            assertThat(result.totalPages).isEqualTo(5)
        }
    }

    @Nested
    @DisplayName("알림 읽음 처리 - read()")
    inner class Read {

        @Test
        @DisplayName("[SUCCESS] 알림 읽음 처리 성공")
        fun 읽음_처리_성공() {
            // Arrange
            val notificationId = UUID.randomUUID()
            val notification = createTestNotification(id = notificationId, userId = testUserId, isRead = false)

            whenever(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification))

            // Act
            notificationService.read(notificationId, testUserId)

            // Assert
            assertThat(notification.isRead).isTrue()
            assertThat(notification.readAt).isNotNull()
            verify(notificationRepository).findById(notificationId)
        }

        @Test
        @DisplayName("[SUCCESS] 이미 읽은 알림 재읽음 처리")
        fun 이미_읽은_알림_재읽음_처리() {
            // Arrange
            val notificationId = UUID.randomUUID()
            val originalReadAt = LocalDateTime.now().minusHours(1)
            val notification = createTestNotification(
                id = notificationId,
                userId = testUserId,
                isRead = true,
                readAt = originalReadAt
            )

            whenever(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification))

            // Act
            notificationService.read(notificationId, testUserId)

            // Assert
            assertThat(notification.isRead).isTrue()
            // read()가 호출되면 readAt이 새로운 시간으로 업데이트됨
            assertThat(notification.readAt).isNotEqualTo(originalReadAt)
        }

        @Test
        @DisplayName("[FAIL] 존재하지 않는 알림 읽음 처리 실패")
        fun 존재하지_않는_알림_실패() {
            // Arrange
            val nonExistentId = UUID.randomUUID()

            whenever(notificationRepository.findById(nonExistentId))
                .thenReturn(Optional.empty())

            // Act & Assert
            assertThatThrownBy {
                notificationService.read(nonExistentId, testUserId)
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Notification not found")
        }

        @Test
        @DisplayName("[FAIL] 다른 사용자의 알림 읽음 처리 실패 - FORBIDDEN")
        fun 다른_사용자_알림_권한_없음_실패() {
            // Arrange
            val notificationId = UUID.randomUUID()
            val notification = createTestNotification(id = notificationId, userId = testUserId)

            whenever(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification))

            // Act & Assert
            assertThatThrownBy {
                notificationService.read(notificationId, otherUserId)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN)
        }
    }

    @Nested
    @DisplayName("전체 알림 읽음 처리 - readAll()")
    inner class ReadAll {

        @Test
        @DisplayName("[SUCCESS] 전체 알림 읽음 처리 성공")
        fun 전체_읽음_처리_성공() {
            // Arrange
            val notifications = listOf(
                createTestNotification(title = "알림1", isRead = false),
                createTestNotification(title = "알림2", isRead = false),
                createTestNotification(title = "알림3", isRead = false)
            )

            whenever(notificationRepository.findAllByUserIdAndIsRead(testUserId, false))
                .thenReturn(notifications)
            whenever(notificationRepository.saveAll(notifications))
                .thenReturn(notifications)

            // Act
            notificationService.readAll(testUserId)

            // Assert
            assertThat(notifications).allMatch { it.isRead }
            assertThat(notifications).allMatch { it.readAt != null }
            verify(notificationRepository).findAllByUserIdAndIsRead(testUserId, false)
            verify(notificationRepository).saveAll(notifications)
        }

        @Test
        @DisplayName("[SUCCESS] 읽지 않은 알림이 없을 때 빈 목록 처리")
        fun 빈_목록_처리_성공() {
            // Arrange
            whenever(notificationRepository.findAllByUserIdAndIsRead(testUserId, false))
                .thenReturn(emptyList())
            whenever(notificationRepository.saveAll(emptyList<Notification>()))
                .thenReturn(emptyList())

            // Act
            notificationService.readAll(testUserId)

            // Assert
            verify(notificationRepository).findAllByUserIdAndIsRead(testUserId, false)
            verify(notificationRepository).saveAll(emptyList())
        }

        @Test
        @DisplayName("[SUCCESS] 일부만 읽지 않은 알림 처리")
        fun 일부_읽지_않은_알림_처리_성공() {
            // Arrange
            val unreadNotifications = listOf(
                createTestNotification(title = "읽지 않은 알림", isRead = false)
            )

            whenever(notificationRepository.findAllByUserIdAndIsRead(testUserId, false))
                .thenReturn(unreadNotifications)
            whenever(notificationRepository.saveAll(unreadNotifications))
                .thenReturn(unreadNotifications)

            // Act
            notificationService.readAll(testUserId)

            // Assert
            assertThat(unreadNotifications).allMatch { it.isRead }
            verify(notificationRepository).findAllByUserIdAndIsRead(testUserId, false)
        }
    }

    @Nested
    @DisplayName("알림 응답 변환 테스트")
    inner class NotificationResponseConversion {

        @Test
        @DisplayName("[SUCCESS] 알림 응답 필드 매핑 정확성 검증")
        fun 응답_필드_매핑_검증() {
            // Arrange
            val notificationId = UUID.randomUUID()
            val createdAt = LocalDateTime.of(2024, 1, 15, 10, 30, 0)
            val notification = createTestNotification(
                id = notificationId,
                type = NotificationType.AI_EXPERIENCE_ANALYSIS_SUCCESS,
                title = "AI 경험분석",
                content = "AI 경험분석이 완료되었습니다.",
                url = "/experience/detail/abc-123",
                isRead = false,
                createdAt = createdAt
            )
            val pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            val page = PageImpl(listOf(notification), pageRequest, 1)

            whenever(notificationRepository.findAllByUserId(eq(testUserId), any()))
                .thenReturn(page)

            // Act
            val result = notificationService.find(testUserId, null, 0, 10)

            // Assert
            assertThat(result.list).hasSize(1)
            val response = result.list[0]
            assertThat(response.id).isEqualTo(notificationId)
            assertThat(response.type).isEqualTo(NotificationType.AI_EXPERIENCE_ANALYSIS_SUCCESS)
            assertThat(response.title).isEqualTo("AI 경험분석")
            assertThat(response.content).isEqualTo("AI 경험분석이 완료되었습니다.")
            assertThat(response.url).isEqualTo("/experience/detail/abc-123")
            assertThat(response.isRead).isFalse()
            assertThat(response.readAt).isNull()
            assertThat(response.createdAt).isEqualTo(createdAt)
        }

        @Test
        @DisplayName("[SUCCESS] URL이 null인 알림 응답")
        fun URL_null_알림_응답() {
            // Arrange
            val notification = createTestNotification(url = null)
            val pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            val page = PageImpl(listOf(notification), pageRequest, 1)

            whenever(notificationRepository.findAllByUserId(eq(testUserId), any()))
                .thenReturn(page)

            // Act
            val result = notificationService.find(testUserId, null, 0, 10)

            // Assert
            assertThat(result.list[0].url).isNull()
        }
    }

    @Nested
    @DisplayName("알림 타입별 테스트")
    inner class NotificationTypeTest {

        @Test
        @DisplayName("[SUCCESS] AI 경험분석 성공 알림 타입")
        fun AI_경험분석_성공_타입() {
            // Arrange
            val notification = createTestNotification(
                type = NotificationType.AI_EXPERIENCE_ANALYSIS_SUCCESS
            )
            val pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            val page = PageImpl(listOf(notification), pageRequest, 1)

            whenever(notificationRepository.findAllByUserId(eq(testUserId), any()))
                .thenReturn(page)

            // Act
            val result = notificationService.find(testUserId, null, 0, 10)

            // Assert
            assertThat(result.list[0].type).isEqualTo(NotificationType.AI_EXPERIENCE_ANALYSIS_SUCCESS)
        }

        @Test
        @DisplayName("[SUCCESS] AI 경험분석 실패 알림 타입")
        fun AI_경험분석_실패_타입() {
            // Arrange
            val notification = createTestNotification(
                type = NotificationType.AI_EXPERIENCE_ANALYSIS_FAIL
            )
            val pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            val page = PageImpl(listOf(notification), pageRequest, 1)

            whenever(notificationRepository.findAllByUserId(eq(testUserId), any()))
                .thenReturn(page)

            // Act
            val result = notificationService.find(testUserId, null, 0, 10)

            // Assert
            assertThat(result.list[0].type).isEqualTo(NotificationType.AI_EXPERIENCE_ANALYSIS_FAIL)
        }
    }
}
