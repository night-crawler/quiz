package fm.force.quiz.core.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean


@NoRepositoryBean
interface CustomRepository <T, ID> : CrudRepository<T, ID> {
    fun refresh(t: T)
}
