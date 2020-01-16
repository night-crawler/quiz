package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.dto.TagFullDTO
import fm.force.quiz.core.dto.TagPatchDTO
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.service.TagService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("tags")
class TagController(tagService: TagService) :
    AbstractCRUDController<Tag, TagRepository, TagPatchDTO, TagFullDTO, SearchQueryDTO>(tagService)
