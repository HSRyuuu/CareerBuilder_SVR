package com.hsryuuu.careerbuilder.domain.user.appuser.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.SubscriptionDto
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserDetailResponse
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserPlanDto
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class UserService(
    private val appUserRepository: AppUserRepository,
    private val subscriptionRepository: SubscriptionRepository
) {

    fun getUserDetail(userId: UUID): UserDetailResponse {
        val user = appUserRepository.findById(userId)
            .orElseThrow { GlobalException(ErrorCode.USER_NOT_FOUND) }

        val subscription = subscriptionRepository.findByUserId(userId)

        return UserDetailResponse(
            id = user.id!!,
            email = user.email,
            username = user.username,
            subscription = subscription?.let { sub ->
                SubscriptionDto(
                    status = sub.status.name,
                    startedAt = sub.startedAt,
                    expiredAt = sub.expiredAt,
                    plan = sub.plan.let { plan ->
                        UserPlanDto(
                            name = plan.name,
                            planType = plan.planType,
                            price = plan.price,
                            experienceAnalysisModel = plan.experienceAnalysisModel,
                            experienceAnalysisLimitPerDay = plan.experienceAnalysisLimitPerDay,
                            careerAnalysisModel = plan.careerAnalysisModel,
                            careerAnalysisLimitPerMonth = plan.careerAnalysisLimitPerMonth,
                            resumeModel = plan.resumeModel,
                            resumeLimitPerMonth = plan.resumeLimitPerMonth
                        )
                    }
                )
            }
        )
    }
}
