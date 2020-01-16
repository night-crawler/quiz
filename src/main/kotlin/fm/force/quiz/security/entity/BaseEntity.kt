package fm.force.quiz.security.entity

import fm.force.quiz.common.ObjectId
import java.time.Instant
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
class BaseEntity(
    @Id
    val id: Long = ObjectId.now(),

    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @LastModifiedDate
    val updatedAt: Instant = Instant.now()
)
