package fm.force.quiz.security.service

import fm.force.quiz.security.configuration.JwtConfigurationProperties
import fm.force.quiz.security.jwt.JwtUserDetails
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*


@Service
class JwtProviderService(
        private val config: JwtConfigurationProperties
) {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val key: Key = Keys.hmacShaKeyFor(config.secret.toByteArray())

    fun issue(jwtUserDetails: JwtUserDetails, now: Date = Date()): String {
        logger.debug("Issuing a token for {}", jwtUserDetails)

        val claims = Jwts.claims().apply {
            issuer = this@JwtProviderService.config.issuer
            subject = jwtUserDetails.username
            issuedAt = now
            expiration = Date(now.time + config.expire.toMillis())
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
    fun safeExtractAuthoritiesFromRoles(mayBeRoles: Any?): List<GrantedAuthority> {
        val roles = mayBeRoles as? Collection<*>
        return roles?.map { GrantedAuthority { it.toString() } } ?: listOf()
    }
}
