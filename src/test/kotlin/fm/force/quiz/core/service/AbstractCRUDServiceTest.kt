package fm.force.quiz.core.service

import fm.force.quiz.AbstractBootTest
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.AuthenticationFacade
import fm.force.quiz.security.service.JwtUserDetailsMapper
import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.transaction.support.TransactionTemplate

abstract class AbstractCRUDServiceTest : AbstractBootTest, StringSpec() {

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

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

        Mockito.`when`(authFacade.principal).thenReturn(jwtUserDetailsMapper.of(user))
        Mockito.`when`(authFacade.user).thenReturn(user)
    }
}
