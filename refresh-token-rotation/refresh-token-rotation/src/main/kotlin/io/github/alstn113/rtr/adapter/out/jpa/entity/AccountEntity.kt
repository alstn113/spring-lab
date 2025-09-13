package io.github.alstn113.rtr.adapter.out.jpa.entity

import io.github.alstn113.rtr.domain.account.Account
import jakarta.persistence.*

@Entity
class AccountEntity(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    val name: String,
) : AuditableEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    fun update(account: Account) {
        this.password = account.password
    }

    fun toDomain(): Account {
        return Account.reconstruct(
            id = id,
            email = email,
            password = password,
            name = name,
        )
    }

    companion object {
        fun fromDomain(account: Account): AccountEntity {
            return AccountEntity(
                email = account.email.value,
                password = account.password,
                name = account.name.value,
            )
        }
    }
}