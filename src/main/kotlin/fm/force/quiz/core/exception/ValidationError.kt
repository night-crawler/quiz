package fm.force.quiz.core.exception

import am.ik.yavi.core.ConstraintViolations

import java.lang.RuntimeException

class ValidationError(val violations: ConstraintViolations) : RuntimeException()
