package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Answer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface JpaAnswerRepository : JpaRepository<Answer, Long>, JpaSpecificationExecutor<Answer>, CommonRepository<Answer>
