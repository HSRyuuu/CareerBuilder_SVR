package com.hsryuuu.careerbuilder.domain.log.repository

import com.hsryuuu.careerbuilder.domain.log.UserActionLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserActionLogRepository : JpaRepository<UserActionLog, Long>
