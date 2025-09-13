package io.github.alstn113.rtr.application.service

import io.github.alstn113.rtr.application.port.`in`.ChangePasswordUseCase
import io.github.alstn113.rtr.application.port.`in`.dto.ChangePasswordCommand
import io.github.alstn113.rtr.application.port.out.AccountRepository
import io.github.alstn113.rtr.application.port.out.PasswordEncoder
import io.github.alstn113.rtr.application.port.out.RefreshTokenRegistry
import io.github.alstn113.rtr.application.service.exception.AccountNotFoundException
import io.github.alstn113.rtr.application.service.exception.IncorrectPasswordException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChangePasswordService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRegistry: RefreshTokenRegistry,
) : ChangePasswordUseCase {

    @Transactional
    override fun invoke(command: ChangePasswordCommand) {
        val account = accountRepository.findById(command.accountId)
            ?: throw AccountNotFoundException()

        val passwordMatches = passwordEncoder.matches(command.currentPassword, account.password)
        if (!passwordMatches) {
            throw IncorrectPasswordException()
        }

        val encodedNewPassword = passwordEncoder.encode(command.newPassword)
        val updatedAccount = account.changePassword(encodedNewPassword)
        accountRepository.save(updatedAccount)
        refreshTokenRegistry.revokeAll(accountId = account.id)
    }
}