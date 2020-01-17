package fm.force.quiz.core.controller

import fm.force.quiz.core.service.DifficultyScaleRangeService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("difficultyScaleRanges")
class DifficultyScaleRangeController(service: DifficultyScaleRangeService) : DifficultyScaleRangeControllerType(service)
