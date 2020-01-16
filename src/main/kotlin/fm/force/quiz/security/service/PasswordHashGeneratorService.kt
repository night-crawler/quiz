package fm.force.quiz.security.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@ConditionalOnMissingBean(type = ["PasswordHashGeneratorService"])
class PasswordHashGeneratorService(
    val passwordEncoder: PasswordEncoder
) {
    fun encode(password: String): String = passwordEncoder.encode(password)
    fun matches(rawPassword: CharSequence?, encodedPassword: String?) = passwordEncoder.matches(rawPassword, encodedPassword)
}
