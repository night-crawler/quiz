package fm.force.quiz.core.exception

import am.ik.yavi.core.ConstraintViolations

open class ValidationError(val violations: ConstraintViolations) : QuizException(null)

class ArbitraryFieldValidationError(
    val fieldName: String,
    val violatedValue: Any? = null,
    message: String
) : QuizException(message)

class NestedValidationError(
    val prefix: String = "",
    violations: ConstraintViolations
) : ValidationError(violations)
