package com.hsryuuu.careerbuilder.application.security

import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*


class UserPrincipal(
    val id: UUID?,
    private val username: String,
    private val password: String,
    private val nickname: String,
    private val email: String,
    private val role: UserRole
) : UserDetails {

    companion object {
        fun from(appUser: AppUser): UserPrincipal {
            return UserPrincipal(
                id = appUser.id,
                username = appUser.username,
                password = appUser.password,
                nickname = appUser.nickname,
                email = appUser.email,
                role = UserRole.ROLE_USER
            )
        }

        fun toUserInfo(userPrincipal: UserPrincipal): UserInfo = UserInfo(
            id = userPrincipal.id,
            username = userPrincipal.username,
            nickname = userPrincipal.nickname,
            email = userPrincipal.email,
            role = userPrincipal.role
        )
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority(role.name))

    override fun getUsername(): String = username

    override fun getPassword(): String = password

    /** ✅ 계정 상태 관련 */
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}