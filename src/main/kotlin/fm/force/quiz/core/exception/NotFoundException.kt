package fm.force.quiz.core.exception

import kotlin.reflect.KClass
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not found")
open class NotFoundException(val id: Any?, klass: KClass<out Any>) :
    QuizException("${klass.simpleName} with id $id not found")
