package com.hsryuuu.careerbuilder.generator

import java.util.*

object TestDataGenerator {

    const val TEST_EMAIL_SUFFIX = "@test.com"
    const val TEST_USERNAME_PREFIX = "test"

    fun generateTestEmail(): String {
        return UUID.randomUUID().toString() + TEST_EMAIL_SUFFIX
    }

    fun generateTestUsername(): String {
        return TEST_USERNAME_PREFIX + UUID.randomUUID().toString()
    }

}