package fm.force.quiz.security.jwt

import fm.force.quiz.configuration.EmptyConfig
import io.kotlintest.specs.*
import io.kotlintest.matchers.*
import io.kotlintest.matchers.string.include
import io.kotlintest.should
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component


// TODO: how to make it run faster?
//   the problem is it can't load its fucking context when using ContextConfiguration
//@ContextConfiguration(classes = [(EmptyConfig::class)])
@SpringBootTest
//@ComponentScan("fm.force")
//@ImportAutoConfiguration(EmptyConfig::class)
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
