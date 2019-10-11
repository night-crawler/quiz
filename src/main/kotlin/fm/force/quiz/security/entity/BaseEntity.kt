package fm.force.quiz.security.entity

import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.lang.reflect.Field
import java.time.Instant
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
class BaseEntity(
        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        val id: Long? = null,

        @CreatedDate
        val createdAt: Instant = Instant.now(),

        @LastModifiedDate
        val updatedAt: Instant = Instant.now()
) {

        private fun getAllFields(klass: Class<*>) : Set<Field> {
                val currentClassFields = klass.declaredFields.toSet()
                return klass.superclass?.let { this.getAllFields(it).union(currentClassFields) } ?: currentClassFields
        }

        val fieldNames get() = getAllFields(this.javaClass).map { it.name }
}
