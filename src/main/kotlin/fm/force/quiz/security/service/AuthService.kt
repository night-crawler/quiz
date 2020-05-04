package fm.force.quiz.security.service

import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.security.configuration.properties.JwtConfigurationProperties
import fm.force.quiz.security.configuration.properties.PasswordConfigurationProperties
import fm.force.quiz.security.dto.ActivateAccountDTO
import fm.force.quiz.security.dto.ActivationSuccessDTO
import fm.force.quiz.security.dto.JwtAccessTokenDTO
import fm.force.quiz.security.dto.JwtResponseTokensDTO
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.jwt.JwtUserDetails
import fm.force.quiz.security.repository.UserRepository
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.lang.IllegalStateException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class AuthService(
    private val transactionTemplate: TransactionTemplate,
    private val userRepository: UserRepository,
    private val jwtUserDetailsMapper: JwtUserDetailsMapper,
    private val hashGeneratorService: PasswordHashGeneratorService,
    private val jwtProviderService: JwtProviderService,
    private val passwordConfigurationProperties: PasswordConfigurationProperties,
    private val conf: JwtConfigurationProperties
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): JwtUserDetails =
        jwtUserDetailsMapper.of(getUser(username))

    fun getUser(email: String?): User {
        email ?: throw UsernameNotFoundException("Null user names are not supported")
        return userRepository
            .findByEmailOrUsername(email, email)
            ?: throw UsernameNotFoundException("Username $email was not found")
    }

    fun register(
        request: RegisterRequestDTO,
        // Applying of Transactional annotation to any method here
        // probably wraps the whole class and breaks the evaluating of kotlin the default values.
        // If isActive is set to passwordConfigurationProperties.userIsEnabledAfterCreation,
        // it throws a NPE here. This is why null.
        isActive: Boolean? = null
    ): User {
        val user = User(
            username = request.email,
            email = request.email,
            password = hashGeneratorService.encode(request.password),
            isActive = isActive ?: passwordConfigurationProperties.userIsEnabledAfterCreation
        )
        return userRepository.save(user)
    }

    fun login(request: LoginRequestDTO): JwtResponseTokensDTO {
        val userDetails = loadUserByUsername(request.email)
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

        val accessToken = jwtProviderService.issueAccessToken(userDetails)
        val refreshToken = jwtProviderService.issueRefreshToken(userDetails)
        return JwtResponseTokensDTO(accessToken = accessToken, refreshToken = refreshToken)
    }

    @Transactional
    fun activate(activateAccountDTO: ActivateAccountDTO): ActivationSuccessDTO {
        val user = userRepository.findById(activateAccountDTO.userId).orElseThrow {
            UsernameNotFoundException("User with id ${activateAccountDTO.userId} was not found!")
        }

        if (user.isActive) {
            throw IllegalStateException("User has already been activated")
        }

        user.isActive = true
        userRepository.save(user)
        return ActivationSuccessDTO("User ${user.email} has been activated")
    }

    fun setupRefreshTokenCookie(response: HttpServletResponse, token: String) =
        response.addCookie(getRefreshTokenCookie(token))

    fun removeRefreshTokenCookie(response: HttpServletResponse) =
        Cookie(conf.refreshTokenCookieName, null)
            .apply {
                path = conf.refreshTokenCookiePath
                isHttpOnly = true
                maxAge = 0
            }
            .let {
                response.addCookie(it)
            }

    internal fun activateUser(user: User) {
        transactionTemplate.execute {
            userRepository.activateUser(user.id)
        }
    }

    internal fun getRefreshTokenCookie(token: String) =
        Cookie(conf.refreshTokenCookieName, token).apply {
            path = conf.refreshTokenCookiePath
            isHttpOnly = true
            maxAge = conf.refreshTokenExpire.seconds.toInt()
        }

    fun issueAccessTokenByRefreshToken(request: HttpServletRequest): JwtAccessTokenDTO {
        val cookie = request.cookies
            ?.find { it.name == conf.refreshTokenCookieName }
            ?: throw BadCredentialsException("Authentication cookie was not found in the request header")
        val username = jwtProviderService.extractUsernameFromRefreshToken(cookie.value)
            ?: throw BadCredentialsException("Refresh token is invalid")

        val userDetails = loadUserByUsername(username)
        return JwtAccessTokenDTO(jwtProviderService.issueAccessToken(userDetails))
    }
}
