package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizSessionAnswer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaQuizSessionAnswerRepository : JpaRepository<QuizSessionAnswer, Long> {
}
