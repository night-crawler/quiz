package fm.force.quiz.security.crypto

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import fm.force.quiz.security.configuration.Argon2PasswordConfigurationProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component


@Component
@ConditionalOnMissingBean(type=["PasswordEncoder"])
class Argon2PasswordEncoder(
        private final val config: Argon2PasswordConfigurationProperties
) : PasswordEncoder {
    private val argon2: Argon2 = Argon2Factory.create()
    private var iterations = config.iterations
    init {
        if (iterations == 0) {
            iterations = Argon2Helper.findIterations(argon2, config.maxMillisecs, config.memory, config.parallelism)
        }
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return argon2.verify(encodedPassword, rawPassword.toString().toByteArray())
    }
    override fun encode(rawPassword: CharSequence?): String {
        return argon2.hash(iterations, config.memory, config.parallelism, rawPassword.toString().toByteArray())
    }
    override fun upgradeEncoding(encodedPassword: String): Boolean {
        return false
    }
}