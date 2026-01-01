package com.hsryuuu.careerbuilder.domain.user.appuser.repository

import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface AppUserRepository : JpaRepository<AppUser, UUID> {
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun findByUsername(username: String): AppUser?
    fun findByEmail(email: String): AppUser?

    fun findByEmailContains(email: String): List<AppUser>
    fun findByUsernameContains(username: String): List<AppUser>

    @Modifying
    @Transactional
    @Query("delete from AppUser where email like concat('%', :email, '%')")
    fun deleteByEmailLike(email: String)

    @Modifying
    @Transactional
    @Query("delete from AppUser where username like concat('%', :username, '%')")
    fun deleteByUsernameLike(username: String)

}