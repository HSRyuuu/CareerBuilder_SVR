package com.hsryuuu.careerbuilder.generator

import java.util.*

object EmailGenerator {

    const val TEST_EMAIL_SUFFIX = "@test.com"

    fun generateTestEmail(): String {
        return UUID.randomUUID().toString() + TEST_EMAIL_SUFFIX
    }

}