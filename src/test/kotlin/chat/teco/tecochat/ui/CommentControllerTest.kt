package chat.teco.tecochat.ui

import chat.teco.tecochat.application.CommentService
import chat.teco.tecochat.createCommentResponse
import chat.teco.tecochat.createUpdateCommentRequest
import chat.teco.tecochat.createWriteCommentRequest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@WebMvcTest(CommentController::class)
class CommentControllerTest : ControllerTest() {

    @MockkBean
    private lateinit var commentService: CommentService

    @Test
    fun `댓글을 작성한다`() {
        every { commentService.write(any(), any()) } returns 1L

        mockMvc.post("/comments") {
            content = objectMapper.writeValueAsString(createWriteCommentRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `댓글을 수정한다`() {
        every { commentService.update(any(), any(), any()) } just runs

        mockMvc.patch("/comments/{commentId}", 1L) {
            content = objectMapper.writeValueAsString(createUpdateCommentRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `댓글을 삭제한다`() {
        every { commentService.delete(any(), any()) } just runs

        mockMvc.delete("/comments/{commentId}", 1L) {
            content = objectMapper.writeValueAsString(createUpdateCommentRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `채팅의 댓글을 조회한다`() {
        every { commentService.findAllByChatId(any()) } returns listOf(createCommentResponse())

        mockMvc.get("/comments") {
            param("chatId", "1")
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }
}
