package fm.force.quiz.security.configuration

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(IAuthenticationExceptionSerializer::class)
class DefaultAuthenticationExceptionSerializerImpl : IAuthenticationExceptionSerializer {
    override fun serialize(authException: AuthenticationException): String {
        return authException.localizedMessage
    }
}
