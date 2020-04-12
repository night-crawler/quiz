package fm.force.quiz.core.controller

import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("profiles")
class ProfileController(
    private val authenticationFacade: AuthenticationFacade
) {
    @GetMapping("current")
    fun current() = authenticationFacade.user.toFullDTO()
}
