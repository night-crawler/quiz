package fm.force.quiz.security.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class JwtUserDetails(
        @get:JvmName("getAuthorities_")
        var authorities:  MutableCollection<out GrantedAuthority>,

        @get:JvmName("getEnabled_")
        var enabled: Boolean,

        @get:JvmName("getUsername_")
        var username: String,

        @get:JvmName("isCredentialsNonExpired_")
        var credentialsNonExpired: Boolean,

        @get:JvmName("getPassword_")
        var password: String,

        @get:JvmName("isAccountNonExpired_")
        var accountNonExpired: Boolean,

        @get:JvmName("isAccountNonLocked_")
        var accountNonLocked: Boolean) : UserDetails {
    /**
     * Returns the authorities granted to the user. Cannot return `null`.
     *
     * @return the authorities, sorted by natural key (never `null`)
     */
    override fun getAuthorities() = authorities

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return `true` if the user is enabled, `false` otherwise
     */
    override fun isEnabled() = enabled

    /**
     * Returns the username used to authenticate the user. Cannot return `null`.
     *
     * @return the username (never `null`)
     */
    override fun getUsername() = username

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return `true` if the user's credentials are valid (ie non-expired),
     * `false` if no longer valid (ie expired)
     */
    override fun isCredentialsNonExpired() = credentialsNonExpired

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    override fun getPassword() = password

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return `true` if the user's account is valid (ie non-expired),
     * `false` if no longer valid (ie expired)
     */
    override fun isAccountNonExpired() = accountNonExpired

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return `true` if the user is not locked, `false` otherwise
     */
    override fun isAccountNonLocked() = accountNonLocked

    override fun toString(): String {
        return "${this::class.java.simpleName}(username=$username)"
    }
}
