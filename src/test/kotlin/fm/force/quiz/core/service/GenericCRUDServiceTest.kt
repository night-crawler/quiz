package fm.force.quiz.core.service

import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.AuthenticationFacade
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean

abstract class GenericCRUDServiceTest(
        val testDataFactory: TestDataFactory,
        val jwtUserDetailsFactoryService: JwtUserDetailsFactoryService
) : StringSpec() {
    @MockBean
    lateinit var authFacade: AuthenticationFacade
    lateinit var user: User
    lateinit var alien: User

    override fun beforeTest(testCase: TestCase) {
        user = testDataFactory.getUser()
        alien = testDataFactory.getUser()

        Mockito.`when`(authFacade.principal).thenReturn(jwtUserDetailsFactoryService.createUserDetails(user))
        Mockito.`when`(authFacade.user).thenReturn(user)
    }
}
