package io.github.alstn113.rtr.application.port.out

interface PasswordEncoder {

    fun encode(rawPassword: String): String

    fun matches(rawPassword: String, encodedPassword: String): Boolean
}