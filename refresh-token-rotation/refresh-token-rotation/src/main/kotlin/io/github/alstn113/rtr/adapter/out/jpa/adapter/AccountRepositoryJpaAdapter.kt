package io.github.alstn113.rtr.adapter.out.jpa.adapter

import io.github.alstn113.rtr.adapter.out.jpa.entity.AccountEntity
import io.github.alstn113.rtr.adapter.out.jpa.repository.AccountJpaRepository
import io.github.alstn113.rtr.application.port.out.AccountRepository
import io.github.alstn113.rtr.application.port.out.dto.AccountSummary
import io.github.alstn113.rtr.domain.account.Account
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AccountRepositoryJpaAdapter(
    private val accountJpaRepository: AccountJpaRepository,
    private val em: EntityManager,
) : AccountRepository {

    @Transactional
    override fun save(account: Account): Account {
        if (account.id == 0L) {
            val entity = AccountEntity.fromDomain(account)
            em.persist(entity)
            return entity.toDomain()
        }
        val entity = accountJpaRepository.findByIdOrNull(account.id)
            ?: throw EntityNotFoundException()
        entity.update(account)
        return entity.toDomain()
    }

    override fun findById(id: Long): Account? {
        return accountJpaRepository.findByIdOrNull(id)
            ?.toDomain()
    }

    override fun findByEmail(email: String): Account? {
        return accountJpaRepository.findByEmail(email)
            ?.toDomain()
    }

    override fun findSummaryById(id: Long): AccountSummary? {
        return accountJpaRepository.findSummaryById(id)
    }

    override fun existsByEmail(email: String): Boolean {
        return accountJpaRepository.existsByEmail(email)
    }
}