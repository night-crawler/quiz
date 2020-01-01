package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.TagPatchDTO
import fm.force.quiz.core.dto.TagFullDTO
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.service.TagService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("tags")
class TagController(tagService: TagService)
    : AbstractCRUDController<Tag, JpaTagRepository, TagPatchDTO, TagFullDTO>(tagService)
