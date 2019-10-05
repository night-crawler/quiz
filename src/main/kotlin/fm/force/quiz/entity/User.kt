package fm.force.quiz.entity

import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        val id: Long? = null,

        val username: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val email: String,

        val password: String? = null,

        val isActive: Boolean = false,

        val createdAt: Instant = Instant.now()
)
