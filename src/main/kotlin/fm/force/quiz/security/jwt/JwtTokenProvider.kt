package fm.force.quiz.security.jwt

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider {
    @Value("\${jwt.secret}")
    private var secret: String = ""

    fun sayStuff() {
        println("!!!!!!!!!!!! $secret")
    }
}
