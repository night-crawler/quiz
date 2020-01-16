package fm.force.quiz.core.exception

import am.ik.yavi.core.ConstraintViolations

class SortingViolations(fieldName: String, message: String) : ConstraintViolations() {
    init {
        add(SortingViolation(fieldName, message))
    }
}
