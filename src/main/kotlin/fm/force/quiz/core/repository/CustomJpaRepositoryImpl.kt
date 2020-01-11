package fm.force.quiz.core.repository

import fm.force.quiz.core.exception.NotFoundException
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager
import javax.transaction.Transactional

open class CustomJpaRepositoryImpl<T, ID>(
        val entityInformation: JpaEntityInformation<T, ID>,
        val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation, entityManager), CustomJpaRepository<T, ID> {
    @Transactional
    override fun refresh(t: T): T {
        val merged = entityManager.merge(t)
        entityManager.refresh(merged)
        return merged
    }

    override fun getEntity(id: ID): T = findById(id).orElseThrow { NotFoundException(id, this::class) }
    override fun findEntitiesById(ids: Iterable<ID>?): List<T> = ids?.let { findAllById(it) } ?: emptyList()
}
