package fm.force.quiz.core.exception

import am.ik.yavi.core.ConstraintViolations

open class ValidationError(val violations: ConstraintViolations) : RuntimeException()

class ArbitraryValidationError(
    val fieldName: String,
    val violatedValue: Any? = null,
    message: String
) : RuntimeException(message)

class NestedValidationError(
    val prefix: String = "",
    violations: ConstraintViolations
) : ValidationError(violations)
