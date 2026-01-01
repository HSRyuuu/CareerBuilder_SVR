package com.hsryuuu.careerbuilder.domain.experience.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.ai.event.ExperienceAnalysisEvent
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.repository.AiRequestRepository
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ContributionLevel
import com.hsryuuu.careerbuilder.domain.experience.model.entity.Experience
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.model.entity.WorkCategory
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.domain.ai.handler.ExperienceAnalysisEventListener
import com.hsryuuu.careerbuilder.generator.TestDataGenerator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@RecordApplicationEvents
@Transactional
@DisplayName("ExperienceAiService 테스트")
class ExperienceAiServiceTest {

    @Autowired
    private lateinit var experienceAiService: ExperienceAiService

    @Autowired
    private lateinit var experienceRepository: ExperienceRepository

    @Autowired
    private lateinit var aiRequestRepository: AiRequestRepository

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var events: ApplicationEvents

    @MockBean
    private lateinit var experienceAnalysisEventListener: ExperienceAnalysisEventListener

    private lateinit var testUser: AppUser

    @BeforeEach
    fun setUp() {
        testUser = appUserRepository.save(
            AppUser(
                email = TestDataGenerator.generateTestEmail(),
                username = TestDataGenerator.generateTestUsername(),
                password = TestDataGenerator.generateTestPassword()
            )
        )
    }

    @Nested
    @DisplayName("AI 분석 요청 - requestAnalysis()")
    inner class RequestAnalysis {

        @Test
        @DisplayName("[SUCCESS] 분석 요청 성공 및 이벤트 발행")
        fun 분석_요청_성공() {
            // Arrange
            val experience = experienceRepository.save(
                Experience(
                    user = testUser,
                    title = "AI Analysis Target",
                    background = "Background info",
                    periodStart = "2024-01",
                    status = ExperienceStatus.COMPLETED,
                    role = "Developer",
                    category = WorkCategory.PROJECT,
                    contributionLevel = ContributionLevel.MEMBER
                )
            )

            // Act
            val requestDto = experienceAiService.requestAnalysis(testUser.id!!, experience.id!!)

            // Assert
            // 1. AiRequest 반환 확인
            assertThat(requestDto).isNotNull
            assertThat(requestDto.userId).isEqualTo(testUser.id)
            assertThat(requestDto.referenceId).isEqualTo(experience.id)
            assertThat(requestDto.status).isEqualTo(AiRequestStatus.PENDING)

            // 2. 이벤트 발행 확인 (ApplicationEvents)
            val eventCount = events.stream(ExperienceAnalysisEvent::class.java)
                .filter { it.userId == testUser.id && it.experienceId == experience.id && it.aiRequestId == requestDto.id }
                .count()
            
            assertThat(eventCount).isEqualTo(1)
        }

        @Test
        @DisplayName("[FAIL] 존재하지 않는 경험 ID")
        fun 존재하지_않는_경험() {
            // Arrange
            val invalidId = UUID.randomUUID()

            // Act & Assert
            assertThatThrownBy {
                experienceAiService.requestAnalysis(testUser.id!!, invalidId)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_NOT_FOUND)
        }

        @Test
        @DisplayName("[FAIL] 타인의 경험에 대한 요청")
        fun 타인의_경험_요청() {
            // Arrange
            val otherUser = appUserRepository.save(
                AppUser(
                    email = TestDataGenerator.generateTestEmail(),
                    username = "other",
                    password = "password"
                )
            )
            val experience = experienceRepository.save(
                Experience(
                    user = otherUser,
                    title = "Other's Experience",
                    background = "Background",
                    periodStart = "2024-01",
                    status = ExperienceStatus.COMPLETED,
                    role = "Role"
                )
            )

            // Act & Assert
            assertThatThrownBy {
                experienceAiService.requestAnalysis(testUser.id!!, experience.id!!)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN)
        }

        @Test
        @DisplayName("[FAIL] 작성 미완료 상태인 경험")
        fun 작성_미완료_경험() {
            // Arrange
            val experience = experienceRepository.save(
                Experience(
                    user = testUser,
                    title = "Incomplete Experience",
                    background = "Background",
                    periodStart = "2024-01",
                    status = ExperienceStatus.INCOMPLETE,
                    role = "Role"
                )
            )

            // Act & Assert
            assertThatThrownBy {
                experienceAiService.requestAnalysis(testUser.id!!, experience.id!!)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_INCOMPLETE)
        }

        @Test
        @DisplayName("[FAIL] 이미 분석 완료된 경험")
        fun 이미_분석된_경험() {
            // Arrange
            val experience = experienceRepository.save(
                Experience(
                    user = testUser,
                    title = "Already Analyzed",
                    background = "Background",
                    periodStart = "2024-01",
                    status = ExperienceStatus.AI_ANALYZED,
                    role = "Role"
                )
            )

            // Act & Assert
            assertThatThrownBy {
                experienceAiService.requestAnalysis(testUser.id!!, experience.id!!)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AI_EXPERIENCE_ANALYSIS_ALREADY_EXISTS)
        }
    }
}
