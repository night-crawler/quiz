package fm.force.quiz.security.jwt

import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import javax.servlet.http.HttpServletRequest


abstract class JwtAuthProviderService {
    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository

    abstract fun authorizeRequest(request: HttpServletRequest?) : Authentication
}

