package fm.force.quiz.security.dto

data class ActivateAccountDTO(
    val userId: Long,
    val activationCode: String
)
