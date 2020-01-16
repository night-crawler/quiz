package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "quiz_session_answers")
data class QuizSessionAnswer(
    @ManyToOne val owner: User,
    @ManyToOne val quiz: Quiz,
    @ManyToOne val quizSession: QuizSession,
    @ManyToOne val question: Question,
    @ManyToOne val answer: Answer
) : BaseQuizEntity()
