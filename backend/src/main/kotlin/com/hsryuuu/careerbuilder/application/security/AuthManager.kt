package com.hsryuuu.careerbuilder.application.security

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
        getCurrentUserId()
            ?: UUID.fromString("e5185f5b-a92b-4dd1-a8a2-4319a4244aa5")//throw GlobalException(ErrorCode.UNAUTHORIZED)
}