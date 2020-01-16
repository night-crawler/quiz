package fm.force.quiz.security.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtUserDetails(
    @get:JvmName("getAuthorities_")
    var authorities: Collection<GrantedAuthority> = listOf(),

    @get:JvmName("getEnabled_")
    var enabled: Boolean = false,

    @get:JvmName("getUsername_")
    var username: String,

    @get:JvmName("isCredentialsNonExpired_")
    var credentialsNonExpired: Boolean = false,

    @get:JvmName("getPassword_")
    var password: String = "",

    @get:JvmName("isAccountNonExpired_")
    var accountNonExpired: Boolean = false,

    @get:JvmName("isAccountNonLocked_")
    var accountNonLocked: Boolean = false,

    var id: Long? = null
) : UserDetails {

    override fun getAuthorities() = authorities
    override fun isEnabled() = enabled
    override fun getUsername() = username
    override fun isCredentialsNonExpired() = credentialsNonExpired
    override fun getPassword() = password
    override fun isAccountNonExpired() = accountNonExpired
    override fun isAccountNonLocked() = accountNonLocked

    fun isUsable() = isAccountNonExpired && isAccountNonLocked && isCredentialsNonExpired && isEnabled

    override fun toString(): String {
        return "${this::class.java.simpleName}(username=$username)"
    }
}
