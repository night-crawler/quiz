package fm.force.quiz.core.entity

import fm.force.quiz.common.ObjectId
import java.time.Instant
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
class BaseQuizEntity(
    @Id
    val id: Long = ObjectId.now(),

    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @LastModifiedDate
    var updatedAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseQuizEntity) return false

        if (id != other.id) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}
