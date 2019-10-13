package fm.force.quiz.security.jwt

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class JwtTokenProvider {
    @Value("\${jwt.secret}")
    private lateinit var secret: String

}
