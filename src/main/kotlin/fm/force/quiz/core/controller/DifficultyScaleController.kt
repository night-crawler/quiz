package fm.force.quiz.core.controller

import fm.force.quiz.core.service.DifficultyScaleService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("difficultyScales")
class DifficultyScaleController(difficultyScaleService: DifficultyScaleService) :
    DifficultyScaleControllerType(service = difficultyScaleService)
