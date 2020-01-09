package fm.force.quiz.security.repository

import fm.force.quiz.security.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface JpaRoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: String): Role?
}
