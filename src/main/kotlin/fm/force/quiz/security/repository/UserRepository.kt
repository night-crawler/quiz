package fm.force.quiz.security.repository

import fm.force.quiz.security.entity.User

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository
import org.springframework.transaction.support.TransactionTemplate


@Repository
interface JpaUserRepository : JpaRepository<User, Long>

@Repository
class UserRepository(
        private val userRepository: JpaUserRepository,
        private val transactionTemplate: TransactionTemplate
) {

}
