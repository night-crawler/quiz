package fm.force.quiz.core.repository

import javax.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface CustomJpaRepository<T, ID> : JpaRepository<T, ID> {
    @Transactional
    fun refresh(t: T): T

    fun getEntity(id: ID): T

    fun findEntitiesById(ids: Iterable<ID>?): List<T>
}
