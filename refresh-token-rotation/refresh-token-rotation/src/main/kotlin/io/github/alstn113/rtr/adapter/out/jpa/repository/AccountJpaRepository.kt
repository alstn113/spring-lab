package io.github.alstn113.rtr.adapter.out.jpa.repository

import io.github.alstn113.rtr.adapter.out.jpa.entity.AccountEntity
import io.github.alstn113.rtr.application.port.out.dto.AccountSummary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AccountJpaRepository : JpaRepository<AccountEntity, Long> {

    fun findByEmail(email: String): AccountEntity?

    fun existsByEmail(email: String): Boolean

    @Query(
        """
        SELECT new io.github.alstn113.rtr.application.port.out.dto.AccountSummary(
            account.id,
            account.email,
            account.name,
            account.createdAt,
            account.updatedAt
        )
        FROM AccountEntity account
        WHERE account.id = :id
    """
    )
    fun findSummaryById(id: Long): AccountSummary?
}