package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository


@Repository
interface JpaQuizQuestionRepository : JpaRepository<QuizQuestion, Long>, JpaSpecificationExecutor<QuizQuestion>, CommonRepository<QuizQuestion>
