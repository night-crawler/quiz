package fm.force.quiz.repository

import fm.force.quiz.entity.Question

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository
import org.springframework.transaction.support.TransactionTemplate


// JpaRepository наследуется уже от PagingAndSortingRepository
@Repository
interface JpaQuestionRepository : JpaRepository<Question, Long>

@Repository
class QuestionRepository(
        private val questionRepository: JpaQuestionRepository,
        private val transactionTemplate: TransactionTemplate
) {

}
