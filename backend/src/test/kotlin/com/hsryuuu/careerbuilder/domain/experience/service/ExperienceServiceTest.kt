package com.hsryuuu.careerbuilder.domain.experience.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.ai.model.ScoreMetrics
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiExperienceAnalysis
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiExperienceSectionAnalysis
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiRequest
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.ai.repository.AiExperienceAnalysisRepository
import com.hsryuuu.careerbuilder.domain.ai.repository.AiRequestRepository
import com.hsryuuu.careerbuilder.domain.experience.model.dto.CreateExperienceRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.CreateSectionRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.UpdateExperienceRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.UpdateSectionRequest
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ContributionLevel
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.model.entity.SectionKind
import com.hsryuuu.careerbuilder.domain.experience.model.entity.WorkCategory
import com.hsryuuu.careerbuilder.domain.experience.model.type.ExperienceSortKey
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.generator.TestDataGenerator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional
@DisplayName("경험 서비스 테스트")
class ExperienceServiceTest {

    @Autowired
    private lateinit var experienceService: ExperienceService

    @Autowired
    private lateinit var experienceRepository: ExperienceRepository

    @Autowired
    private lateinit var aiExperienceAnalysisRepository: AiExperienceAnalysisRepository

    @Autowired
    private lateinit var aiRequestRepository: AiRequestRepository

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    private lateinit var testUser: AppUser
    private lateinit var otherUser: AppUser

    @BeforeEach
    fun setUp() {
        // 테스트용 사용자 생성
        testUser = appUserRepository.save(
            AppUser(
                email = TestDataGenerator.generateTestEmail(),
                username = TestDataGenerator.generateTestUsername(),
                password = TestDataGenerator.generateTestPassword()
            )
        )

        otherUser = appUserRepository.save(
            AppUser(
                email = TestDataGenerator.generateTestEmail(),
                username = TestDataGenerator.generateTestUsername(),
                password = TestDataGenerator.generateTestPassword()
            )
        )
    }

    @Nested
    @DisplayName("경험 생성 - createExperience()")
    inner class CreateExperience {
        @Test
        @DisplayName("[SUCCESS] 섹션 포함 성공")
        fun 섹션_포함_생성_성공() {
            // Arrange
            val request = CreateExperienceRequest(
                title = "테스트 경험",
                background = "테스트 조직",
                periodStart = "2024-01",
                periodEnd = "2024-12",
                keyAchievements = "영향 요약",
                goalSummary = "목표 요약",
                status = ExperienceStatus.INCOMPLETE,
                role = "개발자",
                category = WorkCategory.PROJECT,
                contributionLevel = ContributionLevel.OWNER,
                skills = "Kotlin, Spring Boot",
                sections = listOf(
                    CreateSectionRequest(
                        kind = SectionKind.SITUATION,
                        title = "배경",
                        content = "프로젝트 배경 설명",
                        sortOrder = 0
                    ),
                    CreateSectionRequest(
                        kind = SectionKind.TASK,
                        title = "실행 내용",
                        content = "프로젝트 실행 내용",
                        sortOrder = 1
                    ),
                    CreateSectionRequest(
                        kind = SectionKind.ACHIEVEMENT,
                        title = "결과",
                        content = "프로젝트 결과",
                        sortOrder = 2
                    )
                )
            )

            // Act
            val response = experienceService.createExperience(testUser.id!!, request)

            // Assert
            assertThat(response.id).isNotNull
            assertThat(response.userId).isEqualTo(testUser.id)
            assertThat(response.title).isEqualTo("테스트 경험")
            assertThat(response.background).isEqualTo("테스트 조직")
            assertThat(response.periodStart).isEqualTo("2024-01")
            assertThat(response.periodEnd).isEqualTo("2024-12")
            assertThat(response.keyAchievements).isEqualTo("영향 요약")
            assertThat(response.goalSummary).isEqualTo("목표 요약")
            assertThat(response.status).isEqualTo(ExperienceStatus.INCOMPLETE)
            assertThat(response.role).isEqualTo("개발자")
            assertThat(response.category).isEqualTo(WorkCategory.PROJECT)
            assertThat(response.contributionLevel).isEqualTo(ContributionLevel.OWNER)
            assertThat(response.skills).isEqualTo("Kotlin, Spring Boot")
            assertThat(response.sections.size).isEqualTo(3)
        }

        @Test
        @DisplayName("[SUCCESS] 섹션 없이 성공")
        fun 섹션_없이_생성_성공() {
            // Arrange
            val request = CreateExperienceRequest(
                title = "테스트 경험",
                background = "테스트 조직",
                periodStart = "2024-01",
                periodEnd = "2024-12",
                keyAchievements = "영향 요약",
                goalSummary = "목표 요약",
                status = ExperienceStatus.INCOMPLETE,
                role = "개발자",
                category = WorkCategory.PROJECT,
                contributionLevel = ContributionLevel.OWNER,
                skills = "Kotlin, Spring Boot",
                sections = emptyList()
            )

            // Act
            val response = experienceService.createExperience(testUser.id!!, request)

            // Assert
            assertThat(response.id).isNotNull
            assertThat(response.userId).isEqualTo(testUser.id)
            assertThat(response.title).isEqualTo("테스트 경험")
            assertThat(response.background).isEqualTo("테스트 조직")
            assertThat(response.periodStart).isEqualTo("2024-01")
            assertThat(response.periodEnd).isEqualTo("2024-12")
            assertThat(response.keyAchievements).isEqualTo("영향 요약")
            assertThat(response.goalSummary).isEqualTo("목표 요약")
            assertThat(response.status).isEqualTo(ExperienceStatus.INCOMPLETE)
            assertThat(response.role).isEqualTo("개발자")
            assertThat(response.category).isEqualTo(WorkCategory.PROJECT)
            assertThat(response.contributionLevel).isEqualTo(ContributionLevel.OWNER)
            assertThat(response.skills).isEqualTo("Kotlin, Spring Boot")
            assertThat(response.sections).isEmpty()
        }

        @Test
        @DisplayName("[FAIL] 기간 순서가 잘못되어 실패")
        fun 기간_순서가_잘못되어_실패() {
            // Arrange
            val request = CreateExperienceRequest(
                title = "테스트 경험",
                periodStart = "2024-12",
                periodEnd = "2024-01",
                background = "배경",
                role = "역할"
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.createExperience(testUser.id!!, request)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
        }

        @Test
        @DisplayName("[FAIL] 필수값(제목) 길이 미달 실패")
        fun 필수값_누락_제목_길이_미달_실패() {
            // Arrange
            val request = CreateExperienceRequest(
                title = "짧은", // 5자 미만
                periodStart = "2024-01",
                background = "배경",
                role = "역할"
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.createExperience(testUser.id!!, request)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_TITLE_REQUIRED)
        }

        @Test
        @DisplayName("[FAIL] 필수값(배경) 누락 실패")
        fun 필수값_누락_배경_없음_실패() {
            // Arrange
            val request = CreateExperienceRequest(
                title = "정상적인 제목입니다",
                periodStart = "2024-01",
                background = "", // 빈 값
                role = "역할"
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.createExperience(testUser.id!!, request)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_BACKGROUND_REQUIRED)
        }

        @Test
        @DisplayName("[FAIL] 필수값(역할) 누락 실패")
        fun 필수값_누락_역할_없음_실패() {
            // Arrange
            val request = CreateExperienceRequest(
                title = "정상적인 제목입니다",
                periodStart = "2024-01",
                background = "배경",
                role = "" // 빈 값
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.createExperience(testUser.id!!, request)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_ROLE_REQUIRED)
        }
    }

    @Nested
    @DisplayName("경험 검색 - searchExperience()")
    inner class SearchExperience {
        @Test
        @DisplayName("[SUCCESS] 조건 검색 성공")
        fun 검색_성공() {
            // Arrange
            experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "Backend Development",
                    periodStart = "2023-01",
                    status = ExperienceStatus.COMPLETED,
                    background = "백엔드 팀",
                    role = "백엔드 개발자"
                )
            )
            experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "Frontend Development",
                    periodStart = "2023-06",
                    status = ExperienceStatus.INCOMPLETE,
                    background = "프론트엔드 팀",
                    role = "프론트엔드 개발자"
                )
            )

            // Act
            val result = experienceService.searchExperience(
                userId = testUser.id!!,
                searchKeyword = "Backend",
                status = null,
                page = 0,
                pageSize = 10,
                sort = ExperienceSortKey.UPDATED_AT
            )

            // Assert
            assertThat(result.list).hasSize(1)
            assertThat(result.list[0].title).isEqualTo("Backend Development")
        }
    }

    @Nested
    @DisplayName("경험 조회 - getExperience()")
    inner class GetExperience {
        @Test
        @DisplayName("[SUCCESS] 본인 경험 조회 성공")
        fun 조회_성공() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "My Experience",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )

            // Act
            val result = experienceService.getExperience(created.id, testUser.id!!)

            // Assert
            assertThat(result.id).isEqualTo(created.id)
            assertThat(result.title).isEqualTo("My Experience")
        }

        @Test
        @DisplayName("[FAIL] 존재하지 않는 경험 조회 실패")
        fun 존재하지_않는_경험_실패() {
            // Arrange
            val nonExistentId = UUID.randomUUID()

            // Act & Assert
            assertThatThrownBy {
                experienceService.getExperience(nonExistentId, testUser.id!!)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_NOT_FOUND)
        }

        @Test
        @DisplayName("[FAIL] 다른 사용자의 경험 조회 실패")
        fun 권한_없음_실패() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "User1 Experience",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.getExperience(created.id, otherUser.id!!)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN)
        }
    }

    @Nested
    @DisplayName("경험 및 AI 분석 결과 조회 - getExperienceWithAIAnalysisResult()")
    inner class GetExperienceWithAIAnalysisResult {
        @Test
        @DisplayName("[SUCCESS] 가장 최신 결과만 조회")
        fun 조회_성공() {
            // Arrange
            // 1. Experience 생성
            val createdExp = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "AI Analyzed Experience",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할",
                    sections = listOf(
                        CreateSectionRequest(title = "Section 1", content = "Content 1")
                    )
                )
            )
            val sectionId = createdExp.sections[0].id

            // 2. AiRequest 생성
            val aiRequest = aiRequestRepository.save(
                AiRequest(
                    userId = testUser.id!!,
                    requestType = AiRequestType.EXPERIENCE_ANALYSIS
                )
            )

            // 3. 이전 AI Analysis 생성
            aiExperienceAnalysisRepository.save(
                AiExperienceAnalysis(
                    requestId = aiRequest.id,
                    experienceId = createdExp.id,
                    totalScore = 50,
                    scoreMetrics = ScoreMetrics(50, 50, 50, 50),
                    overallSummary = "Old summary",
                    overallFeedback = "Old feedback"
                )
            )

            Thread.sleep(10) // 시간차를 두기 위함

            // 4. 최신 AI Analysis 생성
            val newAnalysis = AiExperienceAnalysis(
                requestId = aiRequest.id,
                experienceId = createdExp.id,
                totalScore = 85,
                scoreMetrics = ScoreMetrics(80, 80, 90, 90),
                overallSummary = "New summary",
                overallFeedback = "New feedback"
            )

            // 5. Section Analysis 생성 (최신건에만)
            val sectionAnalysis = AiExperienceSectionAnalysis(
                analysis = newAnalysis,
                sectionId = sectionId,
                feedback = "Section feedback",
                improvedContent = "Better content"
            )
            newAnalysis.addSection(sectionAnalysis)

            aiExperienceAnalysisRepository.save(newAnalysis)

            // Act
            val result = experienceService.getExperienceWithAIAnalysisResult(createdExp.id, testUser.id!!)

            // Assert
            assertThat(result.experience.id).isEqualTo(createdExp.id)
            assertThat(result.analysis).isNotNull
            assertThat(result.analysis?.totalScore).isEqualTo(85) // 최신 점수여야 함
            assertThat(result.analysis?.overallSummary).isEqualTo("New summary")

            // Sections 확인
            assertThat(result.sections).hasSize(1)
            assertThat(result.sections[0].section.id).isEqualTo(sectionId)
            assertThat(result.sections[0].analysis).isNotNull
            assertThat(result.sections[0].analysis?.feedback).isEqualTo("Section feedback")
        }
    }

    @Nested
    @DisplayName("AI 분석 결과 존재 여부 확인 - aiAnalysisResultExists()")
    inner class AiAnalysisResultExists {
        @Test
        @DisplayName("[SUCCESS] AI 분석 결과 존재 시 true 반환")
        fun 분석_결과_존재_true() {
            // Arrange
            val createdExp = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "AI Analyzed Experience",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할",
                    sections = listOf(CreateSectionRequest(title = "Section 1", content = "Content 1"))
                )
            )

            val aiRequest = aiRequestRepository.save(
                AiRequest(
                    userId = testUser.id!!,
                    requestType = AiRequestType.EXPERIENCE_ANALYSIS
                )
            )

            aiExperienceAnalysisRepository.save(
                AiExperienceAnalysis(
                    requestId = aiRequest.id,
                    experienceId = createdExp.id,
                    totalScore = 50,
                    scoreMetrics = ScoreMetrics(50, 50, 50, 50),
                    overallSummary = "Summary",
                    overallFeedback = "Feedback"
                )
            )

            // Act
            val result = experienceService.aiAnalysisResultExists(createdExp.id, testUser.id!!)

            // Assert
            assertThat(result).isTrue
        }

        @Test
        @DisplayName("[SUCCESS] AI 분석 결과 미존재 시 false 반환")
        fun 분석_결과_미존재_false() {
            // Arrange
            val createdExp = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "No Analysis Experience",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )

            // Act
            val result = experienceService.aiAnalysisResultExists(createdExp.id, testUser.id!!)

            // Assert
            assertThat(result).isFalse
        }
    }


    @Nested
    @DisplayName("경험 수정 - updateExperience()")
    inner class UpdateExperience {
        @Test
        @DisplayName("[SUCCESS] 정보 및 섹션 업데이트 성공")
        fun 업데이트_성공() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "Old Title",
                    periodStart = "2023-01",
                    background = "Old Background",
                    role = "Old Role",
                    sections = listOf(
                        CreateSectionRequest(title = "Old Section", content = "Old Content")
                    )
                )
            )
            val sectionId = created.sections[0].id

            val updateRequest = UpdateExperienceRequest(
                title = "New Title",
                periodStart = "2023-01",
                background = "New Background",
                role = "New Role",
                sections = listOf(
                    UpdateSectionRequest(
                        id = sectionId,
                        title = "Updated Section",
                        content = "Updated Content"
                    ),
                    UpdateSectionRequest(
                        title = "New Section",
                        content = "New Content"
                    )
                )
            )

            // Act
            val result = experienceService.updateExperience(created.id, testUser.id!!, updateRequest)

            // Assert
            assertThat(result.title).isEqualTo("New Title")
            assertThat(result.sections).hasSize(2)
            val updatedSection = result.sections.find { it.id == sectionId }
            assertThat(updatedSection?.title).isEqualTo("Updated Section")
            val newSection = result.sections.find { it.id != sectionId }
            assertThat(newSection?.title).isEqualTo("New Section")
        }

        @Test
        @DisplayName("[FAIL] 존재하지 않는 경험 수정 실패")
        fun 존재하지_않는_경험_실패() {
            // Arrange
            val nonExistentId = UUID.randomUUID()
            val updateRequest = UpdateExperienceRequest(
                title = "New Title",
                periodStart = "2023-01",
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.updateExperience(nonExistentId, testUser.id!!, updateRequest)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_NOT_FOUND)
        }

        @Test
        @DisplayName("[FAIL] 권한 없는 사용자 수정 실패")
        fun 권한_없음_실패() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "My Experience",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )
            val updateRequest = UpdateExperienceRequest(
                title = "Hacked Title",
                periodStart = "2023-01",
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.updateExperience(created.id, otherUser.id!!, updateRequest)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN)
        }

        @Test
        @DisplayName("[FAIL] 기간 검증 실패")
        fun 기간_오류_실패() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "My Experience",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )
            val updateRequest = UpdateExperienceRequest(
                title = "My Experience",
                periodStart = "2023-12",
                periodEnd = "2023-01",
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.updateExperience(created.id, testUser.id!!, updateRequest)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
        }

        @Test
        @DisplayName("[FAIL] 필수값(제목) 길이 미달 실패")
        fun 필수값_누락_제목_길이_미달_실패() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "정상적인 제목",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )
            val updateRequest = UpdateExperienceRequest(
                title = "짧은",
                periodStart = "2023-01",
                background = "배경",
                role = "역할"
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.updateExperience(created.id, testUser.id!!, updateRequest)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_TITLE_REQUIRED)
        }

        @Test
        @DisplayName("[FAIL] 필수값(배경) 누락 실패")
        fun 필수값_누락_배경_없음_실패() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "정상적인 제목",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )
            val updateRequest = UpdateExperienceRequest(
                title = "정상적인 제목",
                periodStart = "2023-01",
                background = "",
                role = "역할"
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.updateExperience(created.id, testUser.id!!, updateRequest)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_BACKGROUND_REQUIRED)
        }

        @Test
        @DisplayName("[FAIL] 필수값(역할) 누락 실패")
        fun 필수값_누락_역할_없음_실패() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "정상적인 제목",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )
            val updateRequest = UpdateExperienceRequest(
                title = "정상적인 제목",
                periodStart = "2023-01",
                background = "배경",
                role = ""
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.updateExperience(created.id, testUser.id!!, updateRequest)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_ROLE_REQUIRED)
        }
    }

    @Nested
    @DisplayName("경험 삭제 - deleteExperience()")
    inner class DeleteExperience {
        @Test
        @DisplayName("[SUCCESS] 삭제 성공")
        fun 삭제_성공() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "To be deleted",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )

            // Act
            experienceService.deleteExperience(created.id, testUser.id!!)

            // Assert
            assertThatThrownBy {
                experienceService.getExperience(created.id, testUser.id!!)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_NOT_FOUND)
        }

        @Test
        @DisplayName("[FAIL] 존재하지 않는 경험 삭제 실패")
        fun 존재하지_않는_경험_실패() {
            // Arrange
            val nonExistentId = UUID.randomUUID()

            // Act & Assert
            assertThatThrownBy {
                experienceService.deleteExperience(nonExistentId, testUser.id!!)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_NOT_FOUND)
        }

        @Test
        @DisplayName("[FAIL] 권한 없는 사용자 삭제 실패")
        fun 권한_없음_실패() {
            // Arrange
            val created = experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "My Experience",
                    periodStart = "2023-01",
                    background = "배경",
                    role = "역할"
                )
            )

            // Act & Assert
            assertThatThrownBy {
                experienceService.deleteExperience(created.id, otherUser.id!!)
            }
                .isInstanceOf(GlobalException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN)
        }
    }

    @Nested
    @DisplayName("통계 요약 조회 - getStatsSummary()")
    inner class GetStatsSummary {
        @Test
        @DisplayName("[SUCCESS] 통계 요약 조회 성공")
        fun 조회_성공() {
            // Arrange
            experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "Exp 1",
                    periodStart = "2023-01",
                    status = ExperienceStatus.INCOMPLETE,
                    background = "배경",
                    role = "역할"
                )
            )
            experienceService.createExperience(
                testUser.id!!, CreateExperienceRequest(
                    title = "Exp 2 Title",
                    periodStart = "2023-01",
                    status = ExperienceStatus.COMPLETED,
                    background = "배경 설명입니다.",
                    role = "역할 설명입니다.",
                    skills = "Skill1, Skill2",
                    goalSummary = "목표 요약 설명입니다. 30자가 넘어야 점수를 받을 수 있습니다. 조금 더 길게 작성합니다.",
                    keyAchievements = "핵심 성과 설명입니다. 30자가 넘어야 점수를 받을 수 있습니다. 조금 더 길게 작성합니다.",
                    sections = listOf(
                        CreateSectionRequest(
                            title = "섹션 제목",
                            content = "섹션 내용입니다. 20자가 넘어야 점수를 받을 수 있습니다."
                        )
                    )
                )
            )

            // Act
            val stats = experienceService.getStatsSummary(testUser.id!!)

            // Assert
            assertThat(stats.total).isEqualTo(2)
            assertThat(stats.incomplete).isEqualTo(1)
            assertThat(stats.completed).isEqualTo(1)
        }
    }
}