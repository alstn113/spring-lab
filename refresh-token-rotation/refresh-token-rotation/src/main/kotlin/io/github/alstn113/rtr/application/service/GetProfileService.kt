package io.github.alstn113.rtr.application.service

import io.github.alstn113.rtr.application.port.`in`.GetProfileUseCase
import io.github.alstn113.rtr.application.port.`in`.dto.AccountProfile
import io.github.alstn113.rtr.application.port.out.AccountRepository
import io.github.alstn113.rtr.application.service.exception.AccountNotFoundException
import org.springframework.stereotype.Service

@Service
class GetProfileService(
    private val accountRepository: AccountRepository,
) : GetProfileUseCase {

    override fun invoke(accountId: Long): AccountProfile {
        val summary = accountRepository.findSummaryById(id = accountId)
            ?: throw AccountNotFoundException()

        return AccountProfile(
            accountId = summary.id,
            email = summary.email,
            name = summary.name,
            createdAt = summary.createdAt,
            updatedAt = summary.updatedAt,
        )
    }
}