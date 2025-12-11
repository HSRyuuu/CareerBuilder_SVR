package com.hsryuuu.careerbuilder.domain.archivement.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.CreateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.CreateSectionRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementStatus
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.ContributionLevel
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.SectionKind
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.WorkType
import com.hsryuuu.careerbuilder.domain.archivement.repository.AchievementRepository
import com.hsryuuu.careerbuilder.domain.archivement.repository.AchievementSectionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import com.hsryuuu.careerbuilder.generator.TestDataGenerator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@SpringBootTest
@Transactional
@DisplayName("성과 서비스 테스트")
class AchievementServiceTest {

    @Autowired
    private lateinit var achievementService: AchievementService

    @Autowired
    private lateinit var achievementRepository: AchievementRepository

    @Autowired
    private lateinit var achievementSectionRepository: AchievementSectionRepository

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

    @Test
    @DisplayName("[SUCCESS] 성과 생성 - 섹션 포함 성공")
    fun createAchievement_섹션_포함_생성_성공() {
        // Arrange
        val request = CreateAchievementRequest(
            title = "테스트 성과",
            orgName = "테스트 조직",
            durationStart = LocalDate.of(2024, 1, 1),
            durationEnd = LocalDate.of(2024, 12, 31),
            impactSummary = "영향 요약",
            goalSummary = "목표 요약",
            status = AchievementStatus.DRAFT,
            roleTitle = "개발자",
            workType = WorkType.PROJECT,
            contributionLevel = ContributionLevel.OWNER,
            skills = "Kotlin, Spring Boot",
            sections = listOf(
                CreateSectionRequest(
                    kind = SectionKind.CONTEXT,
                    title = "배경",
                    content = "프로젝트 배경 설명",
                    sortOrder = 0
                ),
                CreateSectionRequest(
                    kind = SectionKind.ACTION,
                    title = "실행 내용",
                    content = "프로젝트 실행 내용",
                    sortOrder = 1
                ),
                CreateSectionRequest(
                    kind = SectionKind.RESULT,
                    title = "결과",
                    content = "프로젝트 결과",
                    sortOrder = 2
                )
            )
        )

        // Act
        val response = achievementService.createAchievement(testUser.id!!, request)

        // Assert
        assertThat(response.id).isNotNull
        assertThat(response.userId).isEqualTo(testUser.id)
        assertThat(response.title).isEqualTo("테스트 성과")
        assertThat(response.orgName).isEqualTo("테스트 조직")
        assertThat(response.durationStart).isEqualTo(LocalDate.of(2024, 1, 1))
        assertThat(response.durationEnd).isEqualTo(LocalDate.of(2024, 12, 31))
        assertThat(response.impactSummary).isEqualTo("영향 요약")
        assertThat(response.goalSummary).isEqualTo("목표 요약")
        assertThat(response.status).isEqualTo(AchievementStatus.DRAFT)
        assertThat(response.roleTitle).isEqualTo("개발자")
        assertThat(response.workType).isEqualTo(WorkType.PROJECT)
        assertThat(response.contributionLevel).isEqualTo(ContributionLevel.OWNER)
        assertThat(response.skills).isEqualTo("Kotlin, Spring Boot")
        assertThat(response.sections.size).isEqualTo(3)
    }

    @Test
    @DisplayName("[SUCCESS] 성과 생성 - 섹션 없이 성공")
    fun createAchievement_섹션_없이_생성_성공() {
        // Arrange
        val request = CreateAchievementRequest(
            title = "테스트 성과",
            orgName = "테스트 조직",
            durationStart = LocalDate.of(2024, 1, 1),
            durationEnd = LocalDate.of(2024, 12, 31),
            impactSummary = "영향 요약",
            goalSummary = "목표 요약",
            status = AchievementStatus.DRAFT,
            roleTitle = "개발자",
            workType = WorkType.PROJECT,
            contributionLevel = ContributionLevel.OWNER,
            skills = "Kotlin, Spring Boot",
            sections = emptyList()
        )

        // Act
        val response = achievementService.createAchievement(testUser.id!!, request)

        // Assert
        assertThat(response.id).isNotNull
        assertThat(response.userId).isEqualTo(testUser.id)
        assertThat(response.title).isEqualTo("테스트 성과")
        assertThat(response.orgName).isEqualTo("테스트 조직")
        assertThat(response.durationStart).isEqualTo(LocalDate.of(2024, 1, 1))
        assertThat(response.durationEnd).isEqualTo(LocalDate.of(2024, 12, 31))
        assertThat(response.impactSummary).isEqualTo("영향 요약")
        assertThat(response.goalSummary).isEqualTo("목표 요약")
        assertThat(response.status).isEqualTo(AchievementStatus.DRAFT)
        assertThat(response.roleTitle).isEqualTo("개발자")
        assertThat(response.workType).isEqualTo(WorkType.PROJECT)
        assertThat(response.contributionLevel).isEqualTo(ContributionLevel.OWNER)
        assertThat(response.skills).isEqualTo("Kotlin, Spring Boot")
        assertThat(response.sections).isEmpty()
    }

    @Test
    @DisplayName("[FAIL] 성과 생성 - 존재하지 않는 사용자로 실패")
    fun createAchievement_존재하지_않는_사용자로_실패() {
        // Arrange
        val nonExistentUserId = UUID.randomUUID()
        val request = CreateAchievementRequest(
            title = "테스트 성과",
            durationStart = LocalDate.of(2024, 1, 1)
        )

        // Act & Assert
        assertThatThrownBy {
            achievementService.createAchievement(nonExistentUserId, request)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND)
    }

    @Test
    @DisplayName("[FAIL] 성과 생성 - 기간 순서가 잘못되어 실패")
    fun createAchievement_기간_순서가_잘못되어_실패() {
        // Arrange
        val request = CreateAchievementRequest(
            title = "테스트 성과",
            durationStart = LocalDate.of(2024, 12, 1),
            durationEnd = LocalDate.of(2024, 1, 1)
        )

        // Act & Assert
        assertThatThrownBy {
            achievementService.createAchievement(testUser.id!!, request)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
    }
}
