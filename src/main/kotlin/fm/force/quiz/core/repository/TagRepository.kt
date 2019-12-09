package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Tag_
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root



class SearchCriteria(
        val key: String,
        val operation: String,
        val value: Any
) {
    override fun toString(): String {
        return "SearchCriteria(key='$key', operation='$operation', value=$value)"
    }
}


class TagSpecification(private val searchCriteria: SearchCriteria) : Specification<Tag> {
    override fun toPredicate(root: Root<Tag>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): Predicate? {
        when(searchCriteria.operation) {
            "=" -> return criteriaBuilder.equal(root.get<String>(searchCriteria.key), searchCriteria.value)
        }

        return null
    }

}


@Repository
interface JpaTagRepository : JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {
    @Query("select t.id from Tag t where t.id in :ids and t.owner.id = :ownerId")
    fun findOwnedIds(
            @Param("ids") ids: Collection<Long>,
            @Param("ownerId") ownerId: Long
    ) : Collection<Long>
}
