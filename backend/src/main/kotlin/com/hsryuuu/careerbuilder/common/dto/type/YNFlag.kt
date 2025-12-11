package com.hsryuuu.careerbuilder.common.dto.type

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException

enum class YNFlag(val value: Boolean) {
    Y(true),
    N(false);

    companion object {
        fun fromBoolean(value: Boolean): YNFlag = if (value) Y else N

        fun fromString(value: String): YNFlag =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: throw GlobalException(ErrorCode.BAD_ENUM_REQUEST);

        fun getYesFlags(): List<YNFlag> = listOf(Y)
        fun getNoFlags(): List<YNFlag> = listOf(N)
    }
}