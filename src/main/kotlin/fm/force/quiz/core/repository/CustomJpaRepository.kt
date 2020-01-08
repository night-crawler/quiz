package fm.force.quiz.core.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import javax.transaction.Transactional


@NoRepositoryBean
interface CustomJpaRepository <T, ID> : JpaRepository<T, ID> {
    @Transactional
    fun refresh(t: T) : T
}
