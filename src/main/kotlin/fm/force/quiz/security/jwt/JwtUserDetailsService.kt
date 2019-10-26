package fm.force.quiz.security.jwt

import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
        val jpaUserRepository: JpaUserRepository,
        val jpaUserDetailsFactory: JwtUserDetailsFactory
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
        username ?: throw UsernameNotFoundException("Null usernames are not supported")
        return jpaUserRepository
                .findByEmailOrUsername(username, username)
                ?.let { jpaUserDetailsFactory.createUserDetails(it) }
                ?: throw UsernameNotFoundException("Username $username was not found")
    }
}
