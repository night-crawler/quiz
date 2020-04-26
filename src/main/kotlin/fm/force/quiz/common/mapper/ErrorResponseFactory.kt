package fm.force.quiz.common.mapper

import am.ik.yavi.core.ConstraintViolation
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import fm.force.quiz.common.dto.ErrorMessage
import fm.force.quiz.common.dto.ErrorResponse
import fm.force.quiz.common.dto.FieldError
import fm.force.quiz.core.exception.ArbitraryValidationError
import fm.force.quiz.core.exception.NestedValidationError
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import org.hibernate.exception.ConstraintViolationException
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException

fun ErrorResponse.Companion.of(ex: HttpMessageNotReadableException) =
    when (val cause = ex.mostSpecificCause) {
        is MissingKotlinParameterException -> of(cause)
        is MismatchedInputException -> of(cause)
        else -> of(cause)
    }

fun ErrorResponse.Companion.of(ex: MismatchedInputException) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.VALIDATION,
    errors = ex.path
        // if there's an outlier in an array of Ints: ["a", 1, 2], there will be an empty fieldName in path
        .filter { !it.fieldName.isNullOrBlank() }
        .map { FieldError(it.fieldName, "Field has a wrong type") }
)

fun ErrorResponse.Companion.of(ex: AuthenticationException) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.GENERAL,
    errors = listOf(ErrorMessage(ex.localizedMessage))
)

fun ErrorResponse.Companion.of(ex: Throwable) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.GENERAL,
    errors = listOf(ErrorMessage(ex.localizedMessage))
)

fun ErrorResponse.Companion.of(ex: MissingKotlinParameterException) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.VALIDATION,
    errors = listOf(
        FieldError(
            fieldName = ex.parameter.name ?: "",
            message = "Field is required"
        )
    )
)

fun ErrorResponse.Companion.of(ex: UsernameNotFoundException) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.AUTH,
    errors = listOf(ErrorMessage(ex.localizedMessage))
)

fun ErrorResponse.Companion.of(ex: PropertyReferenceException) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.GENERAL,
    errors = listOf(FieldError(fieldName = ex.propertyName, message = "Unknown field"))
)

fun ErrorResponse.Companion.of(ex: NotFoundException) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.GENERAL,
    errors = listOf(ErrorMessage(ex.localizedMessage))
)

fun ErrorResponse.Companion.of(ex: ValidationError) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.VALIDATION,
    errors = ex.violations.map { it.toFieldError() }
)

val ErrorResponse.Companion.rxFieldWithValues: Regex by lazy {
    // TODO: this is very bad :(
    "\\((?<fieldName>.+)\\)=\\((?<value>.+)\\)".toRegex()
}

fun ErrorResponse.Companion.of(ex: ConstraintViolationException): ErrorResponse {
    val specificMessage = ex.cause?.message ?: ""
    val groups = rxFieldWithValues.find(specificMessage)?.groups
    val commaSeparatedFieldNames = groups?.get("fieldName")?.value
    val commaSeparatedFieldValues = groups?.get("value")?.value

    if (!commaSeparatedFieldNames.isNullOrEmpty() && !commaSeparatedFieldValues.isNullOrEmpty()) {
        // if the violation occurred on a multi-field constraint,
        // there will a comma-separated list of the db fields involved
        // like (quiz_id, question_id)=(27271725837328210, 27271726072212586)

        val errors = commaSeparatedFieldNames
            .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            .map {
                FieldError(
                    fieldName = it,
                    message = "Entity with field name `$commaSeparatedFieldNames` exists:`$commaSeparatedFieldValues`",
                    violatedValue = commaSeparatedFieldValues
                )
            }

        return ErrorResponse(
            exception = ex.javaClass.simpleName,
            type = ErrorResponse.Type.VALIDATION,
            errors = errors
        )
    }

    return ErrorResponse(
        exception = ex.javaClass.simpleName,
        type = ErrorResponse.Type.VALIDATION,
        errors = listOf(
            FieldError(
                fieldName = "",
                message = ex.localizedMessage
            )
        )
    )
}

fun ErrorResponse.Companion.of(ex: MethodArgumentNotValidException): ErrorResponse {
    val globalErrors = ex.bindingResult.globalErrors.map {
        ErrorMessage("${it.objectName}: ${it.defaultMessage ?: ""}")
    }
    val fieldErrors = ex.bindingResult.fieldErrors.map {
        FieldError(
            fieldName = it.field,
            message = it.defaultMessage ?: "",
            violatedValue = it.rejectedValue.toString()
        )
    }
    return ErrorResponse(
        exception = ex.javaClass.simpleName,
        type = ErrorResponse.Type.VALIDATION,
        errors = globalErrors + fieldErrors
    )
}

fun ErrorResponse.Companion.of(ex: DisabledException) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.GENERAL,
    errors = listOf(ErrorMessage(ex.localizedMessage))
)

fun ErrorResponse.Companion.of(ex: NestedValidationError) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.VALIDATION,
    errors = ex.violations.map { it.toFieldError(prefix = ex.prefix) }
)

fun ErrorResponse.Companion.of(ex: ArbitraryValidationError) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.VALIDATION,
    errors = listOf(FieldError(
        fieldName = ex.fieldName,
        message = ex.localizedMessage,
        violatedValue = ex.violatedValue.toString()
    ))
)

fun ConstraintViolation.toFieldError(prefix: String = "") = FieldError(
    fieldName = listOf(prefix, name()).filter { it.isNotEmpty() }.joinToString("."),
    violatedValue = violatedValue().toString(),
    message = message()
)

fun ErrorResponse.Companion.of(ex: HttpMessageNotWritableException) = ErrorResponse(
    exception = ex.javaClass.simpleName,
    type = ErrorResponse.Type.INTERNAL,
    errors = listOf(ErrorMessage(ex.localizedMessage))
)
