package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Question

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import org.springframework.stereotype.Repository


@Repository
interface JpaQuestionRepository : JpaRepository<Question, Long>, JpaSpecificationExecutor<Question>, CommonRepository<Question>
