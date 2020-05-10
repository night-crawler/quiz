package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.ErrorResponse
import fm.force.quiz.common.mapper.of
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * This one must not be in the [ExceptionHandler].
 * Otherwise all exceptions that have a nested cause, like [org.hibernate.exception.ConstraintViolationException],
 * will not be captured by their respective handlers.
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class ServerErrorControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    @ExceptionHandler(Throwable::class)
    fun handleThrowable(ex: Throwable) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.INTERNAL_SERVER_ERROR).also {
            logger.warn(
                "Exception ${ex::class}(${ex.localizedMessage})" +
                    " was serialized using default serializer"
            )
        }
}
