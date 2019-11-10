package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaQuizSessionRepository : JpaRepository<QuizSession, Long> {
}
