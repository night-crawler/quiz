package fm.force.quiz.core.controller

import fm.force.quiz.core.service.TagService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("tags")
class TagController(tagService: TagService) : TagControllerType(tagService)
