package fm.force.quiz.security.jwt

import io.kotlintest.specs.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired


@SpringBootTest
class SampleSpec : WordSpec() {

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    init {
        "bla bla" should {
            "qwe qwe" {
                println(jwtTokenProvider.sayStuff())
            }
        }
    }
}
