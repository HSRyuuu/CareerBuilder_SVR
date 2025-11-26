package com.hsryuuu.careerbuilder.application.type

enum class CurrencyType {
    USD, KRW, EUR;

    companion object {
        fun from(value: String?): CurrencyType? {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }
    }
}