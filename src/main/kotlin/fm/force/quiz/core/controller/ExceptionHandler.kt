package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.ErrorResponse
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
//    @ResponseStatus(HttpStatus.CONFLICT)
//    @ExceptionHandler(value = [DataIntegrityViolationException::class])
//    fun handleConflict(ex: DataIntegrityViolationException): BaseExceptionDTO {
//        return BaseExceptionDTO(ex.message ?: "")
//    }
//
    @ExceptionHandler(NotFoundException::class)
    fun handle404(ex: NotFoundException) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.NOT_FOUND)

    @ExceptionHandler(ValidationError::class)
    fun handleValidationError(ex: ValidationError, request: WebRequest) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.BAD_REQUEST)

    override fun handleHttpMessageNotReadable(
            ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> = ResponseEntity(
            ErrorResponse.of(ex),
            headers,
            status
    )
}
