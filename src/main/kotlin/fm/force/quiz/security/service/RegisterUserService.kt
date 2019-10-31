package fm.force.quiz.security.service

import fm.force.quiz.security.dto.RegisterUserRequestDTO
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.stereotype.Service

@Service
class RegisterUserService(
        val jpaUserRepository: JpaUserRepository,
        val hashGeneratorService: PasswordHashGeneratorService
) {
    fun register(registerUserRequestDTO: RegisterUserRequestDTO) {
        val user = User(
                username = registerUserRequestDTO.email,
                email = registerUserRequestDTO.email,
                password = hashGeneratorService.encode(registerUserRequestDTO.password)
        )
    }
}
