package fm.force.quiz.core.validator

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.core.CustomConstraint
import fm.force.quiz.core.entity.BaseQuizEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * val allAnswersExistPredicate = buildExistencePredicate(jpaAnswerRepository)
 * val questionDTOJpaValidator = ValidatorBuilder.of<CreateQuestionDTO>()
 * .konstraint(CreateQuestionDTO::answers) {
 * predicate(allAnswersExistPredicate as CustomConstraint<Set<Long>?>)
 * }
 * .build()
 */
fun buildExistencePredicate(repository: JpaRepository<out BaseQuizEntity, Long>): CustomConstraint<out Collection<Long>?> {
    return object : CustomConstraint<Collection<Long>?> {
        override fun messageKey() = "custom.existence"
        override fun defaultMessageFormat() = "all \"{0}\" must exist"
        override fun test(idsCollection: Collection<Long>?): Boolean {
            idsCollection ?: return true
            if (idsCollection.isEmpty()) {
                return true
            }
            val ids = idsCollection.toSet()
            return repository.findAllById(ids).map { it.id } == ids
        }
    }
}

val fkValidator = ValidatorBuilder.of(Long::class.java)
        .constraint(Long::toLong, "") {
            it.greaterThan(0).message("Must be positive")
        }
        .build()
