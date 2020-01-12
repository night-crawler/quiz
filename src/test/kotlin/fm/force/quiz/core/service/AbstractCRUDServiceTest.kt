package fm.force.quiz.core.service

import fm.force.quiz.TestConfiguration
import fm.force.quiz.YamlPropertyLoaderFactory
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.AuthenticationFacade
import fm.force.quiz.security.service.JwtUserDetailsMapper
import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
abstract class AbstractCRUDServiceTest : StringSpec() {

    @Autowired
    lateinit var testDataFactory: TestDataFactory

    @Autowired
    lateinit var jwtUserDetailsMapper: JwtUserDetailsMapper

    @MockBean
    lateinit var authFacade: AuthenticationFacade
    lateinit var user: User
    lateinit var alien: User

    override fun beforeTest(testCase: TestCase) {
        user = testDataFactory.getUser()
        alien = testDataFactory.getUser()

        Mockito.`when`(authFacade.principal).thenReturn(jwtUserDetailsMapper.fromUser(user))
        Mockito.`when`(authFacade.user).thenReturn(user)
    }
}
