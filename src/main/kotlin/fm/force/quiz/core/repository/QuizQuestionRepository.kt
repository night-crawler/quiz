package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizQuestion
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface JpaQuizQuestionRepository : CustomJpaRepository<QuizQuestion, Long>, JpaSpecificationExecutor<QuizQuestion>, CommonRepository<QuizQuestion> {
    fun countByQuizId(quizId: Long): Int

    @Modifying
    @Query("update QuizQuestion q set q.seq = q.seq + :diff where q.quiz.id = :quizId and q.seq >= :seq")
    fun updateSeqAfter(@Param("quizId") quizId: Long, @Param("seq") seq: Int, @Param("diff") diff: Int)

    fun findAllByQuizIdOrderBySeq(quizId: Long): List<QuizQuestion>
}
