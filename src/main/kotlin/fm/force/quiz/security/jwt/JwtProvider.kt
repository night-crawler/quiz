package fm.force.quiz.security.jwt

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*


@Component
class JwtProvider {
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
            issuer = this@JwtProvider.issuer
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

    // TODO: validate is a bad name here
    fun validate(token: String): JwtUserDetails? = try {
        val claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token)
        JwtUserDetails(
                authorities = safeExtractAuthoritiesFromRoles(claims.body["roles"]),
                enabled = true,
                username = claims.body.subject,
                credentialsNonExpired = true,
                password = "",
                accountNonExpired = true,
                accountNonLocked = true
        )
    } catch (exc: JwtException) {
        logger.warn("Failed to parse token {}, {}", token, exc)
        null
    } catch (exc: IllegalArgumentException) {
        logger.warn("Illegal token {}, {}", token, exc)
        null
    }

    // TODO: obviously, there's a need to implement custom serializer & deserializer
    //  for particular JwtUserDetails fields, or for a whole class.
    //  But Jackson serializer with a custom ObjectMapper seems to be not that easy to implement.
    fun safeExtractAuthoritiesFromRoles(mayBeRoles: Any?): List<GrantedAuthority> {
        val roles = mayBeRoles as? Collection<*>
        logger.debug("!! $roles")
        return roles?.map { GrantedAuthority { it.toString() } } ?: listOf()
    }
}
