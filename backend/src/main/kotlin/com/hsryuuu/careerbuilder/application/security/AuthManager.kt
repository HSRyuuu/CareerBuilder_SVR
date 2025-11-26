package com.hsryuuu.careerbuilder.application.security

import com.hsryuuu.careerbuilder.application.exception.GlobalException
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuthManager {

    /**
     * 현재 로그인한 사용자 UserInfo
     */
    fun getCurrentUser(): UserInfo? {
        val authentication = SecurityContextHolder.getContext().authentication

        // 인증 정보가 없거나 인증되지 않은 경우
        if (authentication == null || !authentication.isAuthenticated) {
            return null
        }

        val principal = authentication.principal

        if (principal !is UserPrincipal) {
            return null
        }
        return UserPrincipal.toUserInfo(principal)
    }

    fun getCurrentUserId(): UUID? = getCurrentUser()?.id

    fun getCurrentUserIdOrElseThrow(): UUID =
        getCurrentUserId() ?: throw GlobalException(
            HttpStatus.UNAUTHORIZED,
            "로그인이 필요합니다."
        )

}