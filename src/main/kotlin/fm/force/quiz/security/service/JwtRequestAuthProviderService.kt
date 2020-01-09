package fm.force.quiz.security.service

import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import javax.servlet.http.HttpServletRequest


abstract class JwtRequestAuthProviderService {
    @Autowired
    private lateinit var jwtProviderService: JwtProviderService

    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository

    abstract fun authorizeRequest(request: HttpServletRequest?): Authentication
}
