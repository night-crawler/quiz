package fm.force.quiz.security.service

import fm.force.quiz.security.entity.User
import fm.force.quiz.security.jwt.JwtUserDetails

interface JwtUserDetailsFactoryService {
    fun createUserDetails(user: User): JwtUserDetails
    fun createUserDetails(): JwtUserDetails
}
