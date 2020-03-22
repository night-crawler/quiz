package fm.force.quiz.security.configuration

import org.springframework.security.core.AuthenticationException

interface IAuthenticationExceptionSerializer {
    fun serialize(authException: AuthenticationException): String
}
