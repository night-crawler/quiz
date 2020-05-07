package fm.force.quiz.core.controller

import fm.force.quiz.util.JMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

class AuthenticatedTestClient(private val mockMvc: MockMvc, private val token: String) {
    private fun post(uri: String, content: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .header("authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

    private fun patch(uri: String, content: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.patch(uri)
                .header("authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

    fun get(uri: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.get(uri)
                .header("authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

    fun delete(uri: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.delete(uri)
                .header("authorization", "Bearer $token")
        )

    fun post(uri: String, dto: Any) = this.post(uri, JMapper.writeValueAsString(dto))
    fun patch(uri: String, dto: Any) = this.patch(uri, JMapper.writeValueAsString(dto))
}
