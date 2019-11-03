package fm.force.quiz.security.validator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

// May be finish it some day
class ConfigPropertyValidator : ConstraintValidator<CheckPropertyField, Any> {
    @Autowired
    lateinit var env: Environment

    override fun initialize(constraintAnnotation: CheckPropertyField) {
        val ann = constraintAnnotation.annotationClass
        println("! ${constraintAnnotation.annotationClass}")
    }

    override fun isValid(value: Any, context: ConstraintValidatorContext?): Boolean {
        println(value::class.java.declaredFields)
        println(env.get("force.validation.RegisterUserRequestDTO.email"))
        println(env.get("force.security.jwt.secret"))
        println(env.getProperty("force.security.jwt.secret"))
        println(env.getProperty("force.validation.RegisterUserRequestDTO.email"))
        return false
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ConfigPropertyValidator::class])
annotation class CheckPropertyField(
        val message: String = "Invalid",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)
