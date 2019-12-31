package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Question
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository


@Repository
interface JpaQuestionRepository : CustomJpaRepository<Question, Long>, JpaSpecificationExecutor<Question>, CommonRepository<Question>
