package io.github.alstn113.rtr.adapter.out.security.password

import io.github.alstn113.rtr.application.port.out.PasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PasswordConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BcryptPasswordEncoder()
    }
}