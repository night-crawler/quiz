package fm.force.quiz.core.repository

import fm.force.quiz.security.entity.User
import java.util.Optional
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommonRepository<T> {
    fun findByIdAndOwner(id: Long, owner: User): Optional<T>

    @Query("select t.id from #{#entityName} t where t.id in :ids and t.owner.id = :ownerId")
    fun findOwnedIds(
        @Param("ids")
        ids: Collection<Long>,
        @Param("ownerId")
        ownerId: Long
    ): List<Long>

    @Query("select t from #{#entityName} t where t.id in :ids and t.owner.id = :ownerId")
    fun findOwned(
        @Param("ids")
        ids: Collection<Long>,
        @Param("ownerId")
        ownerId: Long
    ): List<T>

    @Query(
        """
        select 
            case when count(c)> 0 then true else false end 
        from #{#entityName} c 
        where 
            c.id = :id and c.owner.id = :ownerId
    """
    )
    fun existsByIdAndOwnerId(
        @Param("id")
        id: Long,
        @Param("ownerId")
        ownerId: Long
    ): Boolean
}
