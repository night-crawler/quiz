package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizQuestion
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface JpaQuizQuestionRepository : CustomJpaRepository<QuizQuestion, Long>, JpaSpecificationExecutor<QuizQuestion>, CommonRepository<QuizQuestion> {
    fun countByQuizId(quizId: Long): Int

    @Modifying
    @Query("""
        update QuizQuestion q 
        set q.seq = q.seq + :diff 
        where q.quiz.id = :quizId and 
              q.seq between :seqFrom and :seqTo
    """)
    fun updateSeqBetween(
            @Param("quizId") quizId: Long,
            @Param("seqFrom") seqFrom: Int,
            @Param("seqTo") seqTo: Int,
            @Param("diff") diff: Int
    )

    fun findAllByQuizIdOrderBySeq(quizId: Long): List<QuizQuestion>
    fun getByQuizIdAndId(quizId: Long, id: Long): Optional<QuizQuestion>
}
