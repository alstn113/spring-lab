package io.github.alstn113.rtr.adapter.out.security.password

import io.github.alstn113.rtr.application.port.out.PasswordEncoder
import org.mindrot.jbcrypt.BCrypt

class BcryptPasswordEncoder : PasswordEncoder {

    override fun encode(rawPassword: String): String {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt())
    }

    override fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, encodedPassword)
    }
}