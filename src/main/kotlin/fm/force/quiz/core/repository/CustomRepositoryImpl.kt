package fm.force.quiz.core.repository

import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager

class CustomRepositoryImpl<T, ID>(
        val entityInformation: JpaEntityInformation<T, ID>,
        val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation, entityManager), CustomRepository<T, ID> {
    override fun refresh(t: T) {
    }
}
