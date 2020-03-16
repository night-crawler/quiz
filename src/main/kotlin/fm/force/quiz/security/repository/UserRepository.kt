package fm.force.quiz.security.repository

import fm.force.quiz.security.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByEmailOrUsername(email: String, username: String): User?
    fun findByEmailAndPassword(email: String, password: String): User?
    fun findUsersByRolesName(rolesName: String): List<User>
    fun findUsersByRolesNameIn(rolesNames: Collection<String>): List<User>
    fun findUserByRolesIsEmpty(): List<User>

    @Modifying
    @Query(
        """
        update User u
        set u.isActive = true
        where u.id = :id
    """
    )
    fun activateUser(
        @Param("id")
        id: Long
    )
}
