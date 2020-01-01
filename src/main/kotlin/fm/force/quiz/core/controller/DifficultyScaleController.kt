package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.DifficultyScaleFullDTO
import fm.force.quiz.core.dto.DifficultyScalePatchDTO
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.repository.JpaDifficultyScaleRepository
import fm.force.quiz.core.service.DifficultyScaleService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("difficultyScales")
class DifficultyScaleController(
        difficultyScaleService: DifficultyScaleService
) : AbstractCRUDController<DifficultyScale, JpaDifficultyScaleRepository, DifficultyScalePatchDTO, DifficultyScaleFullDTO>(
        service = difficultyScaleService
)
