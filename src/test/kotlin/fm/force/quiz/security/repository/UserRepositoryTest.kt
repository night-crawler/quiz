package fm.force.quiz.security.repository

import fm.force.quiz.security.entity.User
import io.kotlintest.provided.fm.force.quiz.security.jwt.JwtConfiguration
import io.kotlintest.specs.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes=[JwtConfiguration::class])
open class UserRepositoryTest : WordSpec() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jpaUserRepository: JpaUserRepository

    init {
        "UserRepository" should {
            "know how to create users" {
                val result = jpaUserRepository.save(User(email = "vasya@force.fm"))
                println("ø String representation: $result")
            }
        }
    }
}
