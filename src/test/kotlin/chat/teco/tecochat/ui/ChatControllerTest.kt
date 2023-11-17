package chat.teco.tecochat.ui

import chat.teco.tecochat.application.ChatService
import chat.teco.tecochat.createUpdateChatTitleRequest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@WebMvcTest(ChatController::class)
class ChatControllerTest : ControllerTest() {

    @MockkBean
    private lateinit var chatService: ChatService

    @Test
    fun `채팅의 제목을 변경한다`() {
        every { chatService.updateTitle(any(), any(), any()) } just runs

        mockMvc.patch("/chats/{id}", 1L) {
            content = objectMapper.writeValueAsString(createUpdateChatTitleRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `채팅을 복사한다`() {
        every { chatService.copy(any(), any()) } returns 1L

        mockMvc.post("/chats/copy/{id}", 1L) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }
    }
}
