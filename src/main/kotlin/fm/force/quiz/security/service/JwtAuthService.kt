package fm.force.quiz.security.service

import fm.force.quiz.security.configuration.PasswordConfigurationProperties
import fm.force.quiz.security.dto.JwtResponseDTO
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.jwt.JwtUserDetails
import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtAuthService(
    private val jpaUserRepository: JpaUserRepository,
    private val jwtUserDetailsMapper: JwtUserDetailsMapper,
    private val hashGeneratorService: PasswordHashGeneratorService,
    private val jwtProviderService: JwtProviderService,
    private val passwordConfigurationProperties: PasswordConfigurationProperties
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails =
        jwtUserDetailsMapper.fromUser(getUser(username))

    fun getUser(username: String?): User {
        username ?: throw UsernameNotFoundException("Null user names are not supported")
        return jpaUserRepository
            .findByEmailOrUsername(username, username)
            ?: throw UsernameNotFoundException("Username $username was not found")
    }

    fun register(
        request: RegisterRequestDTO,
        isActive: Boolean = passwordConfigurationProperties.userIsEnabledAfterCreation
    ): User {
        val user = User(
            username = request.email,
            email = request.email,
            password = hashGeneratorService.encode(request.password),
            isActive = isActive
        )
        return jpaUserRepository.save(user)
    }

    fun authenticate(request: LoginRequestDTO): JwtResponseDTO {
        val userDetails = loadUserByUsername(request.email) as JwtUserDetails
        when {
            !userDetails.isEnabled ->
                throw DisabledException("Account is disabled")
            !userDetails.isAccountNonLocked ->
                throw LockedException("Account was locked")
            !userDetails.isAccountNonExpired ->
                throw AccountExpiredException("Account was expired")
            !userDetails.isCredentialsNonExpired ->
                throw CredentialsExpiredException("Credentials were expired")

            !hashGeneratorService.matches(request.password, userDetails.password) ->
                throw BadCredentialsException("User password is incorrect")
        }

        val token = jwtProviderService.issue(userDetails)
        return JwtResponseDTO(userDetails.username, token)
    }
}
