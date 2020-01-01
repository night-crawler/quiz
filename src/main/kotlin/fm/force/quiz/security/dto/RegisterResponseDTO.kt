package fm.force.quiz.security.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer


data class RegisterResponseDTO(
        @JsonSerialize(using = ToStringSerializer::class)
        val id: Long,
        val username: String
)
