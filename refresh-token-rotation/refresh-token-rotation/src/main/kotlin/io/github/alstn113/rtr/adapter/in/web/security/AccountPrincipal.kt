package io.github.alstn113.rtr.adapter.`in`.web.security

data class AccountPrincipal(
    val accountId: Long,
) {

    companion object {
        private const val ANONYMOUS_ID = -1L
        val ANONYMOUS = AccountPrincipal(ANONYMOUS_ID)
    }

    fun isAnonymous(): Boolean {
        return accountId == ANONYMOUS_ID
    }
}