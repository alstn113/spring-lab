package io.github.alstn113.rtr.domain.account

import io.github.alstn113.rtr.domain.account.vo.AccountEmail
import io.github.alstn113.rtr.domain.account.vo.AccountName

data class Account private constructor(
    val id: Long,
    val email: AccountEmail,
    val password: String,
    val name: AccountName,
) {

    fun changePassword(newPassword: String): Account {
        return this.copy(password = newPassword)
    }

    companion object {
        fun create(email: String, password: String, name: String): Account {
            return Account(
                id = 0L,
                email = AccountEmail(email),
                password = password,
                name = AccountName(name)
            )
        }

        fun reconstruct(id: Long, email: String, password: String, name: String): Account {
            return Account(
                id = id,
                email = AccountEmail(email),
                password = password,
                name = AccountName(name)
            )
        }
    }
}