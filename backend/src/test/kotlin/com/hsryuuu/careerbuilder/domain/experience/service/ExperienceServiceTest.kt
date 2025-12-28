package com.hsryuuu.careerbuilder.domain.experience.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.experience.model.dto.CreateExperienceRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.CreateSectionRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.UpdateExperienceRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.UpdateSectionRequest
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ContributionLevel
import com.hsryuuu.careerbuilder.domain.experience.model.entity.SectionKind
import com.hsryuuu.careerbuilder.domain.experience.model.entity.WorkType
import com.hsryuuu.careerbuilder.domain.experience.model.type.ExperienceSortKey
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceSectionRepository
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
@DisplayName("경험 서비스 테스트")
class ExperienceServiceTest {

    @Autowired
    private lateinit var experienceService: ExperienceService

    @Autowired
    private lateinit var experienceRepository: ExperienceRepository

    @Autowired
    private lateinit var experienceSectionRepository: ExperienceSectionRepository

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
    @DisplayName("[SUCCESS] 경험 생성 - 섹션 포함 성공")
    fun createExperience_섹션_포함_생성_성공() {
        // Arrange
        val request = CreateExperienceRequest(
            title = "테스트 경험",
            orgName = "테스트 조직",
            durationStart = LocalDate.of(2024, 1, 1),
            durationEnd = LocalDate.of(2024, 12, 31),
            impactSummary = "영향 요약",
            goalSummary = "목표 요약",
            status = ExperienceStatus.INCOMPLETE,
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
        val response = experienceService.createExperience(testUser.id!!, request)

        // Assert
        assertThat(response.id).isNotNull
        assertThat(response.userId).isEqualTo(testUser.id)
        assertThat(response.title).isEqualTo("테스트 경험")
        assertThat(response.orgName).isEqualTo("테스트 조직")
        assertThat(response.durationStart).isEqualTo(LocalDate.of(2024, 1, 1))
        assertThat(response.durationEnd).isEqualTo(LocalDate.of(2024, 12, 31))
        assertThat(response.impactSummary).isEqualTo("영향 요약")
        assertThat(response.goalSummary).isEqualTo("목표 요약")
        assertThat(response.status).isEqualTo(ExperienceStatus.INCOMPLETE)
        assertThat(response.roleTitle).isEqualTo("개발자")
        assertThat(response.workType).isEqualTo(WorkType.PROJECT)
        assertThat(response.contributionLevel).isEqualTo(ContributionLevel.OWNER)
        assertThat(response.skills).isEqualTo("Kotlin, Spring Boot")
        assertThat(response.sections.size).isEqualTo(3)
    }

    @Test
    @DisplayName("[SUCCESS] 경험 생성 - 섹션 없이 성공")
    fun createExperience_섹션_없이_생성_성공() {
        // Arrange
        val request = CreateExperienceRequest(
            title = "테스트 경험",
            orgName = "테스트 조직",
            durationStart = LocalDate.of(2024, 1, 1),
            durationEnd = LocalDate.of(2024, 12, 31),
            impactSummary = "영향 요약",
            goalSummary = "목표 요약",
            status = ExperienceStatus.INCOMPLETE,
            roleTitle = "개발자",
            workType = WorkType.PROJECT,
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
        assertThat(response.orgName).isEqualTo("테스트 조직")
        assertThat(response.durationStart).isEqualTo(LocalDate.of(2024, 1, 1))
        assertThat(response.durationEnd).isEqualTo(LocalDate.of(2024, 12, 31))
        assertThat(response.impactSummary).isEqualTo("영향 요약")
        assertThat(response.goalSummary).isEqualTo("목표 요약")
        assertThat(response.status).isEqualTo(ExperienceStatus.INCOMPLETE)
        assertThat(response.roleTitle).isEqualTo("개발자")
        assertThat(response.workType).isEqualTo(WorkType.PROJECT)
        assertThat(response.contributionLevel).isEqualTo(ContributionLevel.OWNER)
        assertThat(response.skills).isEqualTo("Kotlin, Spring Boot")
        assertThat(response.sections).isEmpty()
    }

    @Test
    @DisplayName("[FAIL] 경험 생성 - 존재하지 않는 사용자로 실패")
    fun createExperience_존재하지_않는_사용자로_실패() {
        // Arrange
        val nonExistentUserId = UUID.randomUUID()
        val request = CreateExperienceRequest(
            title = "테스트 경험",
            durationStart = LocalDate.of(2024, 1, 1)
        )

        // Act & Assert
        assertThatThrownBy {
            experienceService.createExperience(nonExistentUserId, request)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND)
    }

    @Test
    @DisplayName("[FAIL] 경험 생성 - 기간 순서가 잘못되어 실패")
    fun createExperience_기간_순서가_잘못되어_실패() {
        // Arrange
        val request = CreateExperienceRequest(
            title = "테스트 경험",
            durationStart = LocalDate.of(2024, 12, 1),
            durationEnd = LocalDate.of(2024, 1, 1)
        )

        // Act & Assert
        assertThatThrownBy {
            experienceService.createExperience(testUser.id!!, request)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
    }

    @Test
    @DisplayName("[SUCCESS] 경험 검색 - 조건 검색 성공")
    fun searchExperience_검색_성공() {
        // Arrange
        val experience1 = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "Backend Development",
                durationStart = LocalDate.of(2023, 1, 1),
                status = ExperienceStatus.COMPLETED
            )
        )
        val experience2 = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "Frontend Development",
                durationStart = LocalDate.of(2023, 6, 1),
                status = ExperienceStatus.INCOMPLETE
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

    @Test
    @DisplayName("[FAIL] 경험 검색 - 존재하지 않는 사용자로 실패")
    fun searchExperience_사용자_없음_실패() {
        // Arrange
        val nonExistentUserId = UUID.randomUUID()

        // Act & Assert
        assertThatThrownBy {
            experienceService.searchExperience(
                userId = nonExistentUserId,
                searchKeyword = null,
                status = null,
                page = 0,
                pageSize = 10,
                sort = ExperienceSortKey.UPDATED_AT
            )
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND)
    }

    @Test
    @DisplayName("[SUCCESS] 경험 조회 - 본인 경험 조회 성공")
    fun getExperience_조회_성공() {
        // Arrange
        val created = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "My Experience",
                durationStart = LocalDate.of(2023, 1, 1)
            )
        )

        // Act
        val result = experienceService.getExperience(created.id, testUser.id!!)

        // Assert
        assertThat(result.id).isEqualTo(created.id)
        assertThat(result.title).isEqualTo("My Experience")
    }

    @Test
    @DisplayName("[FAIL] 경험 조회 - 존재하지 않는 경험 조회 실패")
    fun getExperience_존재하지_않는_경험_실패() {
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
    @DisplayName("[FAIL] 경험 조회 - 다른 사용자의 경험 조회 실패")
    fun getExperience_권한_없음_실패() {
        // Arrange
        val created = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "User1 Experience",
                durationStart = LocalDate.of(2023, 1, 1)
            )
        )

        // Act & Assert
        assertThatThrownBy {
            experienceService.getExperience(created.id, otherUser.id!!)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN)
    }

    @Test
    @DisplayName("[SUCCESS] 경험 수정 - 정보 및 섹션 업데이트 성공")
    fun updateExperience_업데이트_성공() {
        // Arrange
        val created = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "Old Title",
                durationStart = LocalDate.of(2023, 1, 1),
                sections = listOf(
                    CreateSectionRequest(title = "Old Section", content = "Old Content")
                )
            )
        )
        val sectionId = created.sections[0].id

        val updateRequest = UpdateExperienceRequest(
            title = "New Title",
            durationStart = LocalDate.of(2023, 1, 1),
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
    @DisplayName("[FAIL] 경험 수정 - 존재하지 않는 경험 수정 실패")
    fun updateExperience_존재하지_않는_경험_실패() {
        // Arrange
        val nonExistentId = UUID.randomUUID()
        val updateRequest = UpdateExperienceRequest(
            title = "New Title",
            durationStart = LocalDate.of(2023, 1, 1)
        )

        // Act & Assert
        assertThatThrownBy {
            experienceService.updateExperience(nonExistentId, testUser.id!!, updateRequest)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPERIENCE_NOT_FOUND)
    }

    @Test
    @DisplayName("[FAIL] 경험 수정 - 권한 없는 사용자 수정 실패")
    fun updateExperience_권한_없음_실패() {
        // Arrange
        val created = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "My Experience",
                durationStart = LocalDate.of(2023, 1, 1)
            )
        )
        val updateRequest = UpdateExperienceRequest(
            title = "Hacked Title",
            durationStart = LocalDate.of(2023, 1, 1)
        )

        // Act & Assert
        assertThatThrownBy {
            experienceService.updateExperience(created.id, otherUser.id!!, updateRequest)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN)
    }

    @Test
    @DisplayName("[FAIL] 경험 수정 - 기간 검증 실패")
    fun updateExperience_기간_오류_실패() {
        // Arrange
        val created = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "My Experience",
                durationStart = LocalDate.of(2023, 1, 1)
            )
        )
        val updateRequest = UpdateExperienceRequest(
            title = "My Experience",
            durationStart = LocalDate.of(2023, 12, 1),
            durationEnd = LocalDate.of(2023, 1, 1)
        )

        // Act & Assert
        assertThatThrownBy {
            experienceService.updateExperience(created.id, testUser.id!!, updateRequest)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
    }

    @Test
    @DisplayName("[SUCCESS] 경험 삭제 - 삭제 성공")
    fun deleteExperience_삭제_성공() {
        // Arrange
        val created = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "To be deleted",
                durationStart = LocalDate.of(2023, 1, 1)
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
    @DisplayName("[FAIL] 경험 삭제 - 존재하지 않는 경험 삭제 실패")
    fun deleteExperience_존재하지_않는_경험_실패() {
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
    @DisplayName("[FAIL] 경험 삭제 - 권한 없는 사용자 삭제 실패")
    fun deleteExperience_권한_없음_실패() {
        // Arrange
        val created = experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "My Experience",
                durationStart = LocalDate.of(2023, 1, 1)
            )
        )

        // Act & Assert
        assertThatThrownBy {
            experienceService.deleteExperience(created.id, otherUser.id!!)
        }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN)
    }

    @Test
    @DisplayName("[SUCCESS] 통계 요약 조회 성공")
    fun getStatsSummary_조회_성공() {
        // Arrange
        experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "Exp 1",
                durationStart = LocalDate.of(2023, 1, 1),
                status = ExperienceStatus.INCOMPLETE
            )
        )
        experienceService.createExperience(
            testUser.id!!, CreateExperienceRequest(
                title = "Exp 2",
                durationStart = LocalDate.of(2023, 1, 1),
                status = ExperienceStatus.COMPLETED
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