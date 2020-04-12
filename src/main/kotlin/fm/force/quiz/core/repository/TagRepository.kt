package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Tag
import fm.force.quiz.security.entity.User
import java.util.Optional
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : CustomJpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag>, CommonRepository<Tag> {
    fun findByNameAndOwner(name: String, owner: User): Optional<Tag>
}
