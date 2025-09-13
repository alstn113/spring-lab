package io.github.alstn113.rtr.domain.account.vo

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.alstn113.rtr.domain.account.exception.InvalidEmailFormatException

@JvmInline
value class AccountEmail private constructor(val value: String) {

    init {
        validateAccountEmail(value)
    }

    private fun validateAccountEmail(email: String) {
        if (!ACCOUNT_EMAIL_REGEX.matches(email)) {
            throw InvalidEmailFormatException()
        }
    }

    companion object {
        private val ACCOUNT_EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        @JsonCreator
        fun from(email: String): AccountEmail {
            return AccountEmail(email.trim())
        }

        operator fun invoke(email: String): AccountEmail {
            return AccountEmail(email)
        }
    }
}