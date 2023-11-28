package chat.teco.tecochat.ui

import chat.teco.tecochat.application.ChatQueryService
import chat.teco.tecochat.application.ChatService
import chat.teco.tecochat.createUpdateChatTitleRequest
import chat.teco.tecochat.domain.member.Course
import chat.teco.tecochat.query.ChatResponse
import chat.teco.tecochat.query.SearchChatResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@WebMvcTest(ChatController::class)
class ChatControllerTest : ControllerTest() {

    @MockkBean
    private lateinit var chatService: ChatService

    @MockkBean
    private lateinit var chatQueryService: ChatQueryService

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

    @Test
    fun `채팅을 단일 조회한다`() {
        every { chatQueryService.findById(any(), any()) } returns
                ChatResponse(
                    1L,
                    "herb",
                    Course.BACKEND,
                    "title",
                    0,
                    false,
                    LocalDateTime.now(),
                    emptyList(),
                    emptyList()
                )

        mockMvc.get("/chats/{id}", 1L) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `채팅을 검색한다`() {
        every { chatQueryService.search(any(), any()) } returns PageImpl(
            listOf(
                SearchChatResponse(
                    1L,
                    1L,
                    "herb",
                    Course.BACKEND,
                    "title",
                    0,
                    0,
                    1,
                    emptyList(),
                    LocalDateTime.now()
                )
            )
        )
        
        mockMvc.get("/chats", 1L) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }
}
