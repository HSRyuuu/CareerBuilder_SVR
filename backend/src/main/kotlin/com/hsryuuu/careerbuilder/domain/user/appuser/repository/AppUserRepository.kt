package com.hsryuuu.careerbuilder.domain.user.appuser.repository

import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AppUserRepository : JpaRepository<AppUser, UUID> {
    fun existsByUsername(username: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun findByUsername(username: String): AppUser?
}