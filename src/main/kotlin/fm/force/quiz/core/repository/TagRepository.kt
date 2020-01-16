package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Tag
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : CustomJpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag>, CommonRepository<Tag>
