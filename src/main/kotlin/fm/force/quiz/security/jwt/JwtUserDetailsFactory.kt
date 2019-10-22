package fm.force.quiz.security.jwt

import fm.force.quiz.security.entity.User

interface JwtUserDetailsFactory {
    fun createUserDetails(user: User): JwtUserDetails
    fun createUserDetails(): JwtUserDetails
}
