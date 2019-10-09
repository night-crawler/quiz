package fm.force.quiz.security.entity

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

        val username: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val email: String,

        val password: String = "",

        val isActive: Boolean = false,
        val createdAt: Instant = Instant.now()
)
