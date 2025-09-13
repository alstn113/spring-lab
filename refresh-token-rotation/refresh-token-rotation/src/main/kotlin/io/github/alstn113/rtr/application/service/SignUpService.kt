package io.github.alstn113.rtr.application.service

import io.github.alstn113.rtr.application.port.`in`.SignUpUseCase
import io.github.alstn113.rtr.application.port.`in`.dto.SignUpCommand
import io.github.alstn113.rtr.application.port.`in`.dto.SignUpResult
import io.github.alstn113.rtr.application.port.out.AccountRepository
import io.github.alstn113.rtr.application.port.out.PasswordEncoder
import io.github.alstn113.rtr.application.service.exception.AccountEmailExistsException
import io.github.alstn113.rtr.domain.account.Account
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
) : SignUpUseCase {

    @Transactional
    override fun invoke(command: SignUpCommand): SignUpResult {
        val accountExists = accountRepository.existsByEmail(command.email)
        if (accountExists) {
            throw AccountEmailExistsException()
        }

        val hashedPassword = passwordEncoder.encode(command.password)
        val account = Account.create(
            email = command.email,
            password = hashedPassword,
            name = command.name
        )
        val savedAccount = accountRepository.save(account)

        return SignUpResult(
            accountId = savedAccount.id,
            email = savedAccount.email.value,
            name = savedAccount.name.value
        )
    }
}