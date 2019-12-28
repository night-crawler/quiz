package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.DifficultyScaleRangeDTO
import fm.force.quiz.core.dto.PatchDifficultyScaleRangeDTO
import fm.force.quiz.core.entity.DifficultyScaleRange
import fm.force.quiz.core.repository.JpaDifficultyScaleRangeRepository
import fm.force.quiz.core.service.DifficultyScaleRangeService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("difficultyScaleRanges")
class DifficultyScaleRangeController(service: DifficultyScaleRangeService)
    : AbstractCRUDController<DifficultyScaleRange, JpaDifficultyScaleRangeRepository, PatchDifficultyScaleRangeDTO, DifficultyScaleRangeDTO>(service)
