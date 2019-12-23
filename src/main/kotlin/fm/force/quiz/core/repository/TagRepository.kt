package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository


@Repository
interface JpaTagRepository : JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag>, CommonRepository<Tag>
