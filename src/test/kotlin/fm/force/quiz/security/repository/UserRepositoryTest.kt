package fm.force.quiz.security.repository

import fm.force.quiz.security.entity.User
import io.kotlintest.specs.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("test")
@SpringBootTest
class UserRepositoryTest : WordSpec() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jpaUserRepository: JpaUserRepository

    init {
        "UserRepository" should {
            "know how to create users" {
                val result = jpaUserRepository.save(User(email = "vasya@force.fm"))
                println("String representation: $result")
            }
        }
    }
}
