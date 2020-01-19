package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizSessionQuestionAnswer
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface QuizSessionQuestionAnswerRepository :
    CustomJpaRepository<QuizSessionQuestionAnswer, Long>,
    JpaSpecificationExecutor<QuizSessionQuestionAnswer>,
    CommonRepository<QuizSessionQuestionAnswer>
