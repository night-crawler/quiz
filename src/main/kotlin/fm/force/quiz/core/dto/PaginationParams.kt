package fm.force.quiz.core.dto

data class PaginationParams(val page: Int, val pageSize: Int)
data class PaginationQuery(
        val page: Int?,
        val pageSize: Int?
)