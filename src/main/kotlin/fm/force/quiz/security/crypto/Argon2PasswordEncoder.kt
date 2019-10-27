package fm.force.quiz.security.crypto

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import org.springframework.security.crypto.password.PasswordEncoder

open class Argon2PasswordEncoder(
        private val argon2: Argon2 = Argon2Factory.create(),
        private val maxMillisecs: Long = 1000,
        private val memory: Int = 65536,
        private val parallelism: Int = 1,
        private var iterations: Int = 0
) : PasswordEncoder {
    init {
        if (iterations == 0) {
            iterations = Argon2Helper.findIterations(argon2, maxMillisecs, memory, parallelism)
        }
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return true
    }
    override fun encode(rawPassword: CharSequence?): String {
        return ""
    }
    override fun upgradeEncoding(encodedPassword: String): Boolean {
        return false
    }
}