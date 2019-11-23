package fm.force.quiz.core.exception

import fm.force.quiz.core.entity.Question
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import kotlin.reflect.KClass


@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not found")
open class NotFoundException(id: Any, klass: KClass<out Any>) : RuntimeException("${klass.simpleName} with id $id not found")

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not found")
class QuestionNotFound(id: Long) : NotFoundException(id, Question::class)
