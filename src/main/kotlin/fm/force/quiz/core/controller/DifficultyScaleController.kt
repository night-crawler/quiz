package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.DifficultyScaleDTO
import fm.force.quiz.core.dto.PatchDifficultyScaleDTO
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.repository.JpaDifficultyScaleRepository
import fm.force.quiz.core.service.DifficultyScaleService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("difficultyScales")
class DifficultyScaleController(
        difficultyScaleService: DifficultyScaleService
) : AbstractCRUDController<DifficultyScale, JpaDifficultyScaleRepository, PatchDifficultyScaleDTO, DifficultyScaleDTO>(
        service = difficultyScaleService
)
