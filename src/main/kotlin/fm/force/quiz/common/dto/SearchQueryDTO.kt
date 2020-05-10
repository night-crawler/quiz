package fm.force.quiz.common.dto

data class SearchQueryDTO(val query: String?) : DTOSearchMarker

data class QuestionSearchQueryDTO(
    val query: String?,
    val tagSlugs: String? = null,
    val topicSlugs: String? = null
) : DTOSearchMarker
