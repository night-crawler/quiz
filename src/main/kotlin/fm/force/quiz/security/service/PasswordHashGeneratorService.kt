package fm.force.quiz.security.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Service
import fm.force.quiz.security.configuration.PasswordConfigurationProperties
import fm.force.quiz.security.crypto.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Service
@ConditionalOnMissingBean(type=["PasswordHashGeneratorService"])
open class PasswordHashGeneratorService(
        val passwordConfigurationProperties: PasswordConfigurationProperties
) {
    val name = "argon2"

    val passwordEncoder: PasswordEncoder by lazy {
        Argon2PasswordEncoder(
                iterations = passwordConfigurationProperties.iterations
        )
    }

    fun encode(password: String) = passwordEncoder.encode(password)
    fun matches(rawPassword: CharSequence?, encodedPassword: String?) = passwordEncoder.matches(rawPassword, encodedPassword)

    fun serialize() {

    }
}
