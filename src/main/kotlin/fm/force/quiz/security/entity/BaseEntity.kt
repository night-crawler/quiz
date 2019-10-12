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

    private fun getAllFields(klass: Class<*>): Set<Field> {
        val currentClassFields = klass.declaredFields.toSet()
        return klass.superclass?.let { this.getAllFields(it).union(currentClassFields) } ?: currentClassFields
    }

    fun toMap(): Map<String, Any> = getAllFields(javaClass)
            .filter { it.trySetAccessible() }
            .map { it.name to it.get(this@BaseEntity) }
            .toMap()

    val fieldNames get() = getAllFields(this.javaClass).map { it.name }

    override fun toString(): String {
            val falsy = setOf(false, null, "")
        val serializedMap = toMap()
                .entries
                .filter { it.value !in falsy }
                .joinToString { (key, value) -> "$key=$value" }
        return "${javaClass.simpleName}($serializedMap)"
    }
}
