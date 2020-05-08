package fm.force.quiz.core.exception

open class QuizException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)

class QuizImportException(message: String?, cause: Throwable?) : QuizException(message, cause)
