package fm.force.quiz.security.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtUserDetails(
        val ktUsername: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val email: String,
        val ktPassword: String = "",
        val ktEnabled: Boolean = true
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf<GrantedAuthority>()
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return "admin"
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return "admin"
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

}