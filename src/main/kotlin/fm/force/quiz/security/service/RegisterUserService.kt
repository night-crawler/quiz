package fm.force.quiz.security.service

import fm.force.quiz.security.entity.User
import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.stereotype.Service

@Service
class RegisterUserService(
        val jpaUserRepository: JpaUserRepository
) {
    fun register(email: String, password: String) {
        val user = User(
                username = email,
                email = email
        )
    }
}
