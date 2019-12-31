package fm.force.quiz.core.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean


@NoRepositoryBean
interface CustomJpaRepository <T, ID> : JpaRepository<T, ID> {
    fun refresh(t: T)
}
