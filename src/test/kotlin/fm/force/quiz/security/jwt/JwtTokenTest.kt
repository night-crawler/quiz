package fm.force.quiz.security.jwt

import io.kotlintest.provided.fm.force.quiz.security.jwt.JwtConfiguration
import io.kotlintest.specs.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [JwtConfiguration::class])
class JwtTokenTest : WordSpec() {

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    init {
        "JwtTokenTest" should {
            "do something useful" {
            }
        }
    }
}
