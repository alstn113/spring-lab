package io.github.alstn113.rtr.application.port.out

import io.github.alstn113.rtr.application.port.out.dto.AccountSummary
import io.github.alstn113.rtr.domain.account.Account

interface AccountRepository {

    fun save(account: Account): Account

    fun findById(id: Long): Account?

    fun findByEmail(email: String): Account?

    fun findSummaryById(id: Long): AccountSummary?

    fun existsByEmail(email: String): Boolean
}