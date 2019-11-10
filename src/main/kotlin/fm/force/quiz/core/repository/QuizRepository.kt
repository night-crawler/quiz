package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaQuizRepository : JpaRepository<Quiz, Long> {
}
