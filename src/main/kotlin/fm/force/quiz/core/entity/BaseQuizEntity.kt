package fm.force.quiz.core.entity

import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass


@MappedSuperclass
class BaseQuizEntity(
        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        val id: Long? = null,

        @CreatedDate
        val createdAt: Instant = Instant.now(),

        @LastModifiedDate
        var updatedAt: Instant = Instant.now()
)
