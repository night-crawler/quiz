package fm.force.quiz.security.service

import fm.force.quiz.security.configuration.PasswordConfigurationProperties
import fm.force.quiz.security.dto.JwtResponseDTO
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.dto.RegisterResponseDTO
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.jwt.JwtUserDetails
import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.security.authentication.*
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
        val jpaUserRepository: JpaUserRepository,
        val jwtUserDetailsFactoryService: JwtUserDetailsFactoryService,
        val hashGeneratorService: PasswordHashGeneratorService,
        val jwtProviderService: JwtProviderService,
        val passwordConfigurationProperties: PasswordConfigurationProperties
) : UserDetailsService {
    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case-sensitive, or case-insensitive depending on how the
     * implementation instance is configured. In this case, the `UserDetails`
     * object that comes back may have a username that is of a different case than what
     * was actually requested.
     *
     * @param username the username identifying the user whose data is required.
     *
     * @return a fully populated user record (never `null`)
     *
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     * GrantedAuthority
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        username ?: throw UsernameNotFoundException("Null user names are not supported")
        return jpaUserRepository
                .findByEmailOrUsername(username, username)
                ?.let { jwtUserDetailsFactoryService.createUserDetails(it) }
                ?: throw UsernameNotFoundException("Username $username was not found")
    }

    fun register(request: RegisterRequestDTO): RegisterResponseDTO {
        val user = User(
                username = request.email,
                email = request.email,
                password = hashGeneratorService.encode(request.password),
                isActive = passwordConfigurationProperties.userIsEnabledAfterCreation
        )
        val createdUser = jpaUserRepository.save(user)
        return RegisterResponseDTO(createdUser.id!!, createdUser.username)
    }

    fun login(request: LoginRequestDTO): JwtResponseDTO {
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
