package fm.force.quiz.core.repository

import fm.force.quiz.security.entity.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CommonRepository<T> {
    fun findByIdAndOwner(id: Long, owner: User): Optional<T>
    @Query("select t.id from #{#entityName} t where t.id in :ids and t.owner.id = :ownerId")
    fun findOwnedIds(
            @Param("ids") ids: Collection<Long>,
            @Param("ownerId") ownerId: Long
    ): Collection<Long>
}
