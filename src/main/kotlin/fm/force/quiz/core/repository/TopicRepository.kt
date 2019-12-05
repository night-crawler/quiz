package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface JpaTopicRepository : JpaRepository<Topic, Long> {
    @Query("select t.id from Topic t where t.id in :ids and t.owner.id = :ownerId")
    fun findOwnedIds(
            @Param("ids") ids: Collection<Long>,
            @Param("ownerId") ownerId: Long
    ) : Collection<Long>
}
