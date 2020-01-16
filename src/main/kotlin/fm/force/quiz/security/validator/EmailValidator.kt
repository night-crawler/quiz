package fm.force.quiz.security.validator

import fm.force.quiz.security.configuration.PasswordConfigurationProperties
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass
import org.springframework.beans.factory.annotation.Autowired

class EmailValidator : ConstraintValidator<CheckEmail, String> {
    @Autowired
    lateinit var config: PasswordConfigurationProperties

    override fun isValid(value: String?, context: ConstraintValidatorContext) = when {
        value.isNullOrBlank() -> false
        value.length < config.minEmailLength -> false
        value.length > config.maxEmailLength -> false
        !value.contains('@') -> false
        else -> true
    }
}

@Constraint(validatedBy = [EmailValidator::class])
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.TYPE, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckEmail(
    val message: String = "Invalid email",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
