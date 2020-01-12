package fm.force.quiz.security.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.security.entity.User


data class RegisterResponseDTO(
        @JsonSerialize(using = ToStringSerializer::class)
        val id: Long,
        val username: String
)

fun User.toDTO() = RegisterResponseDTO(id = id, username = username)
