package fm.force.quiz.security.dto

data class JwtResponseTokensDTO(
    val accessToken: String,
    val refreshToken: String
)

data class JwtAccessTokenDTO(val accessToken: String)
