package fm.force.quiz.security.service

import fm.force.quiz.security.entity.User
import fm.force.quiz.security.jwt.JwtUserDetails
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(type=["JwtUserDetailsFactoryService"])
class JwtUserDetailsFactoryService {
    fun createUserDetails(): JwtUserDetails {
        return JwtUserDetails(
                authorities = mutableListOf(),
                enabled = true,
                username = "",
                credentialsNonExpired = true,
                password = "",
                accountNonExpired = true,
                accountNonLocked = true
        )
    }

    fun createUserDetails(user: User): JwtUserDetails {
        return JwtUserDetails(
                id = user.id,
                authorities = user.roles.map { GrantedAuthority { it.name } }.toMutableSet(),
                enabled = user.isActive,
                username = user.email,
                credentialsNonExpired = user.isActive,
                password = user.password,
                accountNonExpired = user.isActive,
                accountNonLocked = user.isActive
        )
    }
}