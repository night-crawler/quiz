package fm.force.quiz.core.repository

import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager
import javax.transaction.Transactional

open class CustomJpaRepositoryImpl<T, ID>(
        val entityInformation: JpaEntityInformation<T, ID>,
        val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation, entityManager), CustomJpaRepository<T, ID> {
    @Transactional
    override fun refresh(t: T) : T {
        val merged = entityManager.merge(t)
        entityManager.refresh(merged)
        return merged
    }
}
