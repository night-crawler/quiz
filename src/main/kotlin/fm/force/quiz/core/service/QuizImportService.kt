package fm.force.quiz.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import fm.force.quiz.common.dto.AnswerPatchDTO
import fm.force.quiz.common.dto.QuestionPatchDTO
import fm.force.quiz.common.dto.QuizPatchDTO
import fm.force.quiz.common.dto.TagPatchDTO
import fm.force.quiz.common.dto.TopicPatchDTO
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.exception.QuizImportException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


data class QuizYamlImportDTO(
    val quiz: Quiz
) {
    data class Quiz(
        val questions: List<Question>,
        val title: String,
        val tags: List<String> = listOf(),
        val topics: List<String> = listOf()
    ) {
        data class Question(
            val title: String,
            val text: String,
            val help: String = "",
            val difficulty: Int = 0,
            val answers: List<Answer>,
            val tags: List<String> = listOf(),
            val topics: List<String> = listOf()
        ) {
            data class Answer(
                val correct: Boolean = false,
                val text: String
            )
        }
    }
}


@Service
class QuizImportService(
    private val quizService: QuizService,
    private val questionService: QuestionService,
    private val topicService: TopicService,
    private val tagService: TagService,
    private val answerService: AnswerService
) {
    private val yamlObjectMapper = ObjectMapper(YAMLFactory()).also {
        it.findAndRegisterModules()
    }

    @Transactional
    fun import(rawYaml: String): Quiz {
        val rawQuiz = try {
            yamlObjectMapper.readValue<QuizYamlImportDTO>(rawYaml).quiz
        } catch (exc: Exception) {
            throw QuizImportException("Quiz import failed", exc)
        }
        val topics = resolveTopics(rawQuiz)
        val tags = resolveTags(rawQuiz)

        val questionIds = rawQuiz.questions.map {
            resolveQuestion(it, tags, topics)
        }

        return quizService.create(QuizPatchDTO(
            title = rawQuiz.title,
            tags = rawQuiz.tags.map { tags[it.trim()]!! }.toSet(),
            topics = rawQuiz.topics.map { topics[it.trim()]!! }.toSet(),
            questions = questionIds
        ))
    }

    private fun resolveQuestion(
        rawQuestion: QuizYamlImportDTO.Quiz.Question,
        tags: Map<String, Long>,
        topics: Map<String, Long>
    ): Long {
        val correctAnswerIds = mutableSetOf<Long>()
        val answerIds = rawQuestion.answers.map { rawAnswer ->
            answerService.create(AnswerPatchDTO(rawAnswer.text.trim())).id.also { id ->
                if (rawAnswer.correct)
                    correctAnswerIds.add(id)
            }
        }

        return questionService.create(QuestionPatchDTO(
            title = rawQuestion.title.trim(),
            text = rawQuestion.text.trim(),
            help = rawQuestion.text.trim(),
            difficulty = rawQuestion.difficulty,
            answers = answerIds.toSet(),
            correctAnswers = correctAnswerIds,
            tags = rawQuestion.tags.map { tags[it.trim()]!! }.toSet(),
            topics = rawQuestion.topics.map { topics[it.trim()]!! }.toSet()
        )).id
    }

    private fun resolveTopics(rawQuiz: QuizYamlImportDTO.Quiz): Map<String, Long> {
        return (rawQuiz.topics + rawQuiz.questions.map { it.topics }.flatten())
            .map { it.trim() }
            .map {
                it to topicService.getOrCreate(TopicPatchDTO(it)).first.id
            }.toMap()
    }

    private fun resolveTags(rawQuiz: QuizYamlImportDTO.Quiz): Map<String, Long> {
        return (rawQuiz.tags + rawQuiz.questions.map { it.tags }.flatten())
            .map { it.trim() }
            .map {
                it to tagService.getOrCreate(TagPatchDTO(it)).first.id
            }.toMap()
    }

}
