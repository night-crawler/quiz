package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.TagPatchDTO
import fm.force.quiz.core.dto.toFullDTO
import fm.force.quiz.core.service.TagService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("tags")
class TagController(tagService: TagService) : TagControllerType(tagService) {
    override val service: TagService = tagService

    @PostMapping("getOrCreate")
    fun getOrCreateTag(@RequestBody tagPatchDTO: TagPatchDTO)
        = service.getOrCreateTag(tagPatchDTO).toFullDTO()
}
