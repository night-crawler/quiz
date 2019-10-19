package fm.force.quiz.security.jwt

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*


@Component
class JwtTokenProvider {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var key: Key

    @Value("\${jwt.secret}")
    fun prepareSecretKey(secret: String) {
        key = Keys.hmacShaKeyFor(secret.toByteArray())
    }

    @Value("\${jwt.expire}")
    private val expireTimeoutMs: Long = 0

    @Value("\${jwt.issuer}")
    private lateinit var issuer: String

    fun issue(jwtUserDetails: JwtUserDetails, now: Date = Date()): String {
        logger.debug("Issuing a token for {}", jwtUserDetails)

        val claims = Jwts.claims().apply {
            issuer = this@JwtTokenProvider.issuer
            subject = jwtUserDetails.username
            issuedAt = now
            expiration = Date(now.time + expireTimeoutMs)
        }
        claims["roles"] = jwtUserDetails.authorities.map { it.authority }

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact()
                .also { logger.debug("Issued a token for {}: {}", jwtUserDetails, it) }
    }

    fun validate(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token)
        } catch (exc: JwtException) {
            return false
        } catch (exc: IllegalArgumentException) {
            return false
        }
        return true
    }
}
