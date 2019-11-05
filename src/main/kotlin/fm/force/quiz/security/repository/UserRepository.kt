package fm.force.quiz.security.repository

import fm.force.quiz.security.entity.Role
import fm.force.quiz.security.entity.User

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository


@Repository
interface JpaUserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByEmailOrUsername(email: String, username: String): User?
    fun findByEmailAndPassword(email: String, password: String): User?
    fun findUsersByRolesName(roles_name: String) : List<User>
    fun findUsersByRolesNameIn(roles_names: Collection<String>): List<User>
    fun findUsersByRolesEquals(roles: Collection<Role>): List<User>
    fun findUserByRolesIsEmpty(): List<User>
}
