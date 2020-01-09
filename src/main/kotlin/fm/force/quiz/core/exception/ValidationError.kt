package fm.force.quiz.core.exception

import am.ik.yavi.core.ConstraintViolations

class ValidationError(val violations: ConstraintViolations) : RuntimeException()
