package fm.force.quiz.repository

import fm.force.quiz.entity.User

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository
import org.springframework.transaction.support.TransactionTemplate


// JpaRepository наследуется уже от PagingAndSortingRepository
@Repository
interface JpaUserRepository : JpaRepository<User, Long>

@Repository
class UserRepository(
        private val userRepository: JpaUserRepository,
        private val transactionTemplate: TransactionTemplate
) {

}
