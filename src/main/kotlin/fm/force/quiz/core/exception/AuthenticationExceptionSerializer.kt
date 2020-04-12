package fm.force.quiz.core.exception

import fm.force.quiz.common.dto.ErrorResponse
import fm.force.quiz.common.mapper.of
import fm.force.quiz.security.configuration.IAuthenticationExceptionSerializer
import kotlinx.serialization.json.Json
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component

@Component
class AuthenticationExceptionSerializer(
    private val json: Json
) : IAuthenticationExceptionSerializer {
    override fun serialize(authException: AuthenticationException): String {
        val errorResponse = ErrorResponse.of(authException)
        return json.stringify(ErrorResponse.serializer(), errorResponse)
    }
}
