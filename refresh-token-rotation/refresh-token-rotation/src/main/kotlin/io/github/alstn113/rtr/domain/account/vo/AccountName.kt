package io.github.alstn113.rtr.domain.account.vo

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.alstn113.rtr.domain.account.exception.InvalidNameLengthException

@JvmInline
value class AccountName private constructor(val value: String) {

    init {
        validateAccountName(value)
    }

    private fun validateAccountName(name: String) {
        if (name.length !in MIN_LENGTH..MAX_LENGTH) {
            throw InvalidNameLengthException(MIN_LENGTH, MAX_LENGTH)
        }
    }

    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 20

        @JsonCreator
        fun from(name: String): AccountName {
            return AccountName(name.trim())
        }

        operator fun invoke(name: String): AccountName {
            return AccountName(name)
        }
    }
}