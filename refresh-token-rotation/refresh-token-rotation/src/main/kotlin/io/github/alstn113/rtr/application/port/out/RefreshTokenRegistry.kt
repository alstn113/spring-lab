package io.github.alstn113.rtr.application.port.out

interface RefreshTokenRegistry {

    fun register(accountId: Long, familyId: String, jti: String)

    fun rotate(accountId: Long, familyId: String, oldJti: String, newJti: String): Boolean

    fun revokeFamily(accountId: Long, familyId: String)

    fun revokeAll(accountId: Long)
}