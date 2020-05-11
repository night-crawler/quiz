package fm.force.quiz.common.dto

data class SearchQueryDTO(val query: String?) : DTOSearchMarker

data class QuestionSearchQueryDTO(
    val query: String?,
    val tags: String? = null,
    val topics: String? = null
) : DTOSearchMarker

data class TagSearchQueryDTO(
    val query: String?,
    val slugs: List<String>? = null
) : DTOSearchMarker

data class TopicSearchQueryDTO(
    val query: String?,
    val slugs: List<String>? = null
) : DTOSearchMarker
