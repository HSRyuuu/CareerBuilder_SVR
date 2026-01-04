package com.hsryuuu.careerbuilder.domain.plan.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.common.dto.type.CommonPeriod
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.ai.quota.UsageLimitManager
import com.hsryuuu.careerbuilder.domain.plan.model.entity.Plan
import com.hsryuuu.careerbuilder.domain.plan.model.entity.PlanType
import com.hsryuuu.careerbuilder.domain.plan.model.entity.Subscription
import com.hsryuuu.careerbuilder.domain.plan.repository.PlanRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional
@DisplayName("사용량 서비스 테스트")
class UsageServiceTest {

    @Autowired
    private lateinit var usageService: UsageService

    @Autowired
    private lateinit var subscriptionRepository: SubscriptionRepository

    @Autowired
    private lateinit var planRepository: PlanRepository

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @MockBean
    private lateinit var usageLimitManager: UsageLimitManager

    private lateinit var testUser: AppUser
    private lateinit var testPlan: Plan

    @BeforeEach
    fun setUp() {
        testUser = appUserRepository.save(
            AppUser(
                username = "testuser-" + UUID.randomUUID(),
                email = "test-" + UUID.randomUUID() + "@example.com",
                password = "password"
            )
        )

        testPlan = planRepository.save(
            Plan(
                name = "Test Plan",
                planType = PlanType.BASIC,
                experienceAnalysisModel = "gpt-4o",
                experienceAnalysisLimitPerDay = 10,
                careerAnalysisModel = "gpt-4o",
                careerAnalysisLimitPerMonth = 5,
                resumeModel = "gpt-4o",
                resumeLimitPerMonth = 3
            )
        )
    }

    @Test
    @DisplayName("유저의 구독 사용량을 정확히 조회한다")
    fun getUserSubscriptionUsage_Success() {
        // given
        subscriptionRepository.save(
            Subscription(
                user = testUser,
                plan = testPlan
            )
        )
        val experienceUsage = 3
        given(usageLimitManager.getUsageCount(testUser.id!!, AiRequestType.EXPERIENCE_ANALYSIS))
            .willReturn(experienceUsage)

        // when
        val result = usageService.getUserSubscriptionUsage(testUser.id!!)

        // then
        val experienceDetail = result.usageSummary[AiRequestType.EXPERIENCE_ANALYSIS]!!
        assertThat(experienceDetail.limit).isEqualTo(testPlan.experienceAnalysisLimitPerDay)
        assertThat(experienceDetail.current).isEqualTo(experienceUsage)
        assertThat(experienceDetail.period).isEqualTo(CommonPeriod.DAY)

        val careerDetail = result.usageSummary[AiRequestType.CAREER]!!
        assertThat(careerDetail.limit).isEqualTo(testPlan.careerAnalysisLimitPerMonth)
        assertThat(careerDetail.current).isEqualTo(0L)
        assertThat(careerDetail.period).isEqualTo(CommonPeriod.MONTH)

        val resumeDetail = result.usageSummary[AiRequestType.RESUME]!!
        assertThat(resumeDetail.limit).isEqualTo(testPlan.resumeLimitPerMonth)
        assertThat(resumeDetail.current).isEqualTo(0L)
        assertThat(resumeDetail.period).isEqualTo(CommonPeriod.MONTH)
    }

    @Test
    @DisplayName("구독 정보가 없는 경우 예외가 발생한다")
    fun getUserSubscriptionUsage_NotFound() {
        // given
        val userId = UUID.randomUUID()

        // when & then
        assertThatThrownBy { usageService.getUserSubscriptionUsage(userId) }
            .isInstanceOf(GlobalException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SUBSCRIPTION_NOT_FOUND)
    }
}
