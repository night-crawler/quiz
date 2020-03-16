package fm.force.quiz.security.service

import fm.force.quiz.security.configuration.properties.JwtConfigurationProperties
import fm.force.quiz.security.jwt.JwtUserDetails
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.Date
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service

@Service
class JwtProviderService(
    private val config: JwtConfigurationProperties
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val key: Key = Keys.hmacShaKeyFor(config.secret.toByteArray())
    private val jwtParser: JwtParser = Jwts.parser().setSigningKey(key)

    private fun getCommonClaims(jwtUserDetails: JwtUserDetails, now: Date, expireDate: Date): Claims {
        val claims = Jwts.claims().apply {
            issuer = config.issuer
            subject = jwtUserDetails.username
            issuedAt = now
            expiration = expireDate
        }
        // 2^54 trouble:
        // 9223372036854776000 == 9223372036854775807
        claims["userId"] = "${jwtUserDetails.id}"
        return claims
    }

    fun issueAccessToken(jwtUserDetails: JwtUserDetails, now: Date = Date()): String {
        logger.debug("Issuing an access token for {}", jwtUserDetails)

        // add role information for access token only in case we need it some day (fat chance)
        val claims = getCommonClaims(jwtUserDetails, now, Date(now.time + config.accessTokenExpire.toMillis()))
        claims["roles"] = jwtUserDetails.authorities.map { it.authority }

        return Jwts.builder()
            .setClaims(claims)
            .signWith(key)
            .compact()
            .also { logger.debug("Issued an access token for {}: {}", jwtUserDetails, it) }
    }

    fun issueRefreshToken(jwtUserDetails: JwtUserDetails, now: Date = Date()): String {
        logger.debug("Issuing a refresh token for {}", jwtUserDetails)
        return Jwts.builder()
            .setClaims(getCommonClaims(jwtUserDetails, now, Date(now.time + config.refreshTokenExpire.toMillis())))
            .signWith(key)
            .compact()
            .also { logger.debug("Issued a refresh token for {}: {}", jwtUserDetails, it) }
    }

    fun extractUserDetailsFromAccessToken(token: String): JwtUserDetails? = try {
        val claims = jwtParser.parseClaimsJws(token)
        JwtUserDetails(
            id = safeExtractUserId(claims.body["userId"]),
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

    fun extractUsernameFromRefreshToken(token: String) = try {
        jwtParser.parseClaimsJws(token).body.subject
    } catch (exc: JwtException) {
        logger.warn("Failed to parse token {}, {}", token, exc)
        null
    } catch (exc: IllegalArgumentException) {
        logger.warn("Illegal token {}, {}", token, exc)
        null
    }

    fun safeExtractAuthoritiesFromRoles(mayBeRoles: Any?): List<GrantedAuthority> {
        val roles = mayBeRoles as? Collection<*>
        return roles?.map { GrantedAuthority { it.toString() } } ?: listOf()
    }

    fun safeExtractUserId(mayBeUserId: Any?) = try {
        mayBeUserId.toString().toLong()
    } catch (exc: NumberFormatException) {
        // really we don't need null here
        null
    }
}
