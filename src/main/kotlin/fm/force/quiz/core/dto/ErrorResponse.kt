package fm.force.quiz.core.dto

import am.ik.yavi.core.ConstraintViolation
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import org.hibernate.exception.ConstraintViolationException
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException

fun ConstraintViolation.toFieldError() = FieldError(
        fieldName = name(),
        message = message()
)

data class ErrorMessage(
        val message: String
)

data class FieldError(
        val fieldName: String,
        val message: String,
        val violatedValue: String? = null
)

data class ErrorResponse(
        val exception: String,
        val type: Type = Type.GENERAL,
        val errors: List<Any> = emptyList()
) {
    enum class Type {
        VALIDATION, GENERAL, AUTH
    }

    companion object {
        // TODO: this is very bad :(
        private val rxFieldWithValues = "\\((?<fieldName>.+)\\)=\\((?<value>.+)\\)".toRegex()

        fun of(ex: MethodArgumentNotValidException): ErrorResponse {
            val globalErrors = ex.bindingResult.globalErrors.map {
                ErrorMessage("${it.objectName}: ${it.defaultMessage ?: ""}")
            }
            val fieldErrors = ex.bindingResult.fieldErrors.map {
                FieldError(fieldName = it.field, message = it.defaultMessage ?: "", violatedValue = it.rejectedValue.toString())
            }
            return ErrorResponse(
                    exception = ex.javaClass.simpleName,
                    type = Type.VALIDATION,
                    errors = globalErrors + fieldErrors
            )
        }

        fun of(ex: ConstraintViolationException): ErrorResponse {
            val specificMessage = ex.cause?.message ?: ""
            val groups = rxFieldWithValues.find(specificMessage)?.groups
            val commaSeparatedFieldNames = groups?.get("fieldName")?.value
            val value = groups?.get("value")?.value

            if (!commaSeparatedFieldNames.isNullOrEmpty() && !value.isNullOrEmpty()) {
                // if the violation occurred on a multi-field constraint,
                // there will a comma-separated list of the db fields involved

                val errors = commaSeparatedFieldNames
                        .split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        .map {
                            FieldError(
                                    fieldName = it,
                                    message = "Entity with field name `$commaSeparatedFieldNames` exists: `$value`",
                                    violatedValue = value
                            )
                        }

                return ErrorResponse(
                        exception = ex.javaClass.simpleName,
                        type = Type.VALIDATION,
                        errors = errors
                )
            }

            return ErrorResponse(
                    exception = ex.javaClass.simpleName,
                    type = Type.VALIDATION,
                    errors = listOf(FieldError(
                            fieldName = "",
                            message = ex.localizedMessage
                    ))
            )
        }

        fun of(ex: UsernameNotFoundException) = ErrorResponse(
                exception = ex.javaClass.simpleName,
                type = Type.AUTH,
                errors = listOf(ErrorMessage(ex.localizedMessage))
        )

        fun of(ex: PropertyReferenceException) = ErrorResponse(
                exception = ex.javaClass.simpleName,
                type = Type.GENERAL,
                errors = listOf(FieldError(fieldName = ex.propertyName, message = "Unknown field"))
        )

        fun of(ex: NotFoundException) = ErrorResponse(
                exception = ex.javaClass.simpleName,
                type = Type.GENERAL,
                errors = listOf(ErrorMessage(ex.localizedMessage))
        )

        fun of(ex: ValidationError) = ErrorResponse(
                exception = ex.javaClass.simpleName,
                type = Type.VALIDATION,
                errors = ex.violations.map { it.toFieldError() }
        )

        fun of(ex: MissingKotlinParameterException) = ErrorResponse(
                exception = ex.javaClass.simpleName,
                type = Type.VALIDATION,
                errors = listOf(FieldError(ex.parameter.name ?: "", "Field is required"))
        )

        fun of(ex: Throwable) = ErrorResponse(
                exception = ex.javaClass.simpleName,
                type = Type.GENERAL,
                errors = listOf(ErrorMessage(ex.localizedMessage))
        )

        fun of(ex: MismatchedInputException) = ErrorResponse(
                exception = ex.javaClass.simpleName,
                type = Type.VALIDATION,
                errors = ex.path
                        // if there's an outlier in an array of Ints: ["a", 1, 2], there will be empty fieldName in path
                        .filter { !it.fieldName.isNullOrBlank() }
                        .map { FieldError(it.fieldName, "Field has a wrong type") }
        )

        fun of(ex: HttpMessageNotReadableException) =
                when (val cause = ex.mostSpecificCause) {
                    is MissingKotlinParameterException -> of(cause)
                    is MismatchedInputException -> of(cause)
                    else -> of(cause)
                }
    }
}

