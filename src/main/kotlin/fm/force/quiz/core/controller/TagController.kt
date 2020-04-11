package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.TagFullDTO
import fm.force.quiz.core.dto.TagPatchDTO
import fm.force.quiz.core.service.TagService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("tags")
class TagController(tagService: TagService) : TagControllerType(tagService) {
    override val service: TagService = tagService

    @PostMapping("getOrCreate")
    fun getOrCreateTag(@RequestBody tagPatchDTO: TagPatchDTO): ResponseEntity<TagFullDTO> {
        val (tag, isCreated) = service.getOrCreate(tagPatchDTO)
        val tagFullDTO = service.serializeEntity(tag)
        val status = if (isCreated) HttpStatus.CREATED else HttpStatus.OK
        return ResponseEntity(tagFullDTO, status)
    }
}
