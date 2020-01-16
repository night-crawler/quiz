package fm.force.quiz.core.exception

import am.ik.yavi.core.ConstraintViolation
import am.ik.yavi.message.SimpleMessageFormatter
import java.util.Locale

class SortingViolation(fieldName: String, message: String) : ConstraintViolation(
    fieldName, fieldName, message,
    arrayOf(fieldName),
    SimpleMessageFormatter(),
    Locale.ENGLISH
)
