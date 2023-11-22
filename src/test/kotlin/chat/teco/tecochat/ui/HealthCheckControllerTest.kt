package chat.teco.tecochat.ui

import chat.teco.tecochat.createChatLikeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@WebMvcTest(HealthCheckController::class)
class HealthCheckControllerTest : ControllerTest() {

    @Test
    fun `200 OK를 응답한다`() {
        mockMvc.get("/") {
            content = objectMapper.writeValueAsString(createChatLikeRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }
}
