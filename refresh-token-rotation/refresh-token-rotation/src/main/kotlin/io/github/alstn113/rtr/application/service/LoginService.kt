package io.github.alstn113.rtr.application.service

import io.github.alstn113.rtr.application.port.`in`.LoginUseCase
import io.github.alstn113.rtr.application.port.`in`.dto.LoginCommand
import io.github.alstn113.rtr.application.port.`in`.dto.LoginResult
import io.github.alstn113.rtr.application.port.out.AccountRepository
import io.github.alstn113.rtr.application.port.out.PasswordEncoder
import io.github.alstn113.rtr.application.port.out.RefreshTokenRegistry
import io.github.alstn113.rtr.application.port.out.TokenProvider
import io.github.alstn113.rtr.application.port.out.dto.AccessTokenPayload
import io.github.alstn113.rtr.application.port.out.dto.RefreshTokenPayload
import io.github.alstn113.rtr.application.service.exception.AccountNotFoundException
import io.github.alstn113.rtr.application.service.exception.BadCredentialsException
import io.github.alstn113.rtr.domain.account.Account
import org.springframework.stereotype.Service
import java.util.*

@Service
class LoginService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val refreshTokenRegistry: RefreshTokenRegistry,
) : LoginUseCase {

    override fun invoke(command: LoginCommand): LoginResult {
        val account = authenticateUser(command)
        val (accessToken, refreshToken) = issueTokens(account)

        return LoginResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    private fun authenticateUser(command: LoginCommand): Account {
        val account = accountRepository.findByEmail(command.email)
            ?: throw AccountNotFoundException()

        if (!passwordEncoder.matches(command.password, account.password)) {
            throw BadCredentialsException()
        }
        return account
    }

    private fun issueTokens(account: Account): Pair<String, String> {
        val familyId = UUID.randomUUID().toString()
        val jti = UUID.randomUUID().toString()

        val accessToken = tokenProvider.generateAccessToken(
            AccessTokenPayload(accountId = account.id)
        )
        val refreshToken = tokenProvider.generateRefreshToken(
            RefreshTokenPayload(
                accountId = account.id,
                familyId = familyId,
                jti = jti,
            )
        )
        refreshTokenRegistry.register(
            accountId = account.id,
            familyId = familyId,
            jti = jti,
        )

        return Pair(accessToken, refreshToken)
    }
}