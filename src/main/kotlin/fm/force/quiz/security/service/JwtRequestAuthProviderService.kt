package fm.force.quiz.security.service

import fm.force.quiz.security.repository.UserRepository
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication

abstract class JwtRequestAuthProviderService {
    @Autowired
    private lateinit var jwtProviderService: JwtProviderService

    @Autowired
    private lateinit var userRepository: UserRepository

    abstract fun authenticateRequest(request: HttpServletRequest?): Authentication
}
