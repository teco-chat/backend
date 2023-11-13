package chat.teco.tecochat.ui

import chat.teco.tecochat.application.ChatLikeService
import chat.teco.tecochat.createChatLikeRequest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

@WebMvcTest(ChatLikeController::class)
class ChatLikeControllerTest : ControllerTest() {

    @MockkBean
    private lateinit var chatLikeService: ChatLikeService

    @Test
    fun `좋아요를 누른다`() {
        every { chatLikeService.pushLike(any(), any()) } returns Unit

        mockMvc.post("/chat-likes") {
            content = objectMapper.writeValueAsString(createChatLikeRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }
}
