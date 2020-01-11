package fm.force.quiz.core.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import kotlin.reflect.KClass

abstract class CoreException(message: String, val service: KClass<out Any>) : RuntimeException(message)

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not found")
open class NotFoundException(val id: Any?, klass: KClass<out Any>) : RuntimeException("${klass.simpleName} with id $id not found")

