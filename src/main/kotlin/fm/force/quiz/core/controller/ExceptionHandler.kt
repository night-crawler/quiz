package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.ErrorResponse
import fm.force.quiz.common.mapper.of
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(EmptyResultDataAccessException::class)
    fun handleEmptyResultDataAccessException(ex: EmptyResultDataAccessException) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.NOT_FOUND)

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(ex: UsernameNotFoundException) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.CONFLICT)

    @ExceptionHandler(PropertyReferenceException::class)
    fun handlePropertyReferenceException(ex: PropertyReferenceException) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(NotFoundException::class)
    fun handle404(ex: NotFoundException) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.NOT_FOUND)

    @ExceptionHandler(ValidationError::class)
    fun handleValidationError(ex: ValidationError, request: WebRequest) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(DisabledException::class)
    fun handleDisabledException(ex: DisabledException, request: WebRequest) =
        ResponseEntity(ErrorResponse.of(ex), HttpStatus.FORBIDDEN)

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = ResponseEntity(
        ErrorResponse.of(ex),
        headers,
        status
    )

    override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(ErrorResponse.of(ex), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(ErrorResponse.of(ex), HttpStatus.BAD_REQUEST)
    }
}
