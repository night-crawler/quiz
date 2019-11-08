package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Question

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository
import org.springframework.transaction.support.TransactionTemplate


@Repository
interface JpaQuestionRepository : JpaRepository<Question, Long> {

}

@Repository
class QuestionRepository(
        private val questionRepository: JpaQuestionRepository,
        private val transactionTemplate: TransactionTemplate
) {

}
