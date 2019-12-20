package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Tag
import fm.force.quiz.security.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

interface CommonRepository<T> {
    fun findByIdAndOwner(id: Long, owner: User): Optional<T>
}


@Repository
interface JpaTagRepository : JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag>, CommonRepository<Tag> {
    @Query("select t.id from Tag t where t.id in :ids and t.owner.id = :ownerId")
    fun findOwnedIds(
            @Param("ids") ids: Collection<Long>,
            @Param("ownerId") ownerId: Long
    ): Collection<Long>
}
