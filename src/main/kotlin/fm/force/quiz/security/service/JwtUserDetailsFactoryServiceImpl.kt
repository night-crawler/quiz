package fm.force.quiz.security.service

import fm.force.quiz.security.entity.User
import fm.force.quiz.security.jwt.JwtUserDetails
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component


@Component
class JwtUserDetailsFactoryServiceImpl : JwtUserDetailsFactoryService {
    override fun createUserDetails(): JwtUserDetails {
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

    override fun createUserDetails(user: User): JwtUserDetails {
        return JwtUserDetails(
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
