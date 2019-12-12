package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Answer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JpaAnswerRepository : JpaRepository<Answer, Long> {
    @Query("select a.id from Answer a where a.id in :ids and a.owner.id = :ownerId")
    fun findOwnedIds(
            @Param("ids") ids: Collection<Long>,
            @Param("ownerId") ownerId: Long
    ) : Collection<Long>
}
