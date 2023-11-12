package chat.teco.tecochat.ui

import chat.teco.tecochat.application.MemberService
import chat.teco.tecochat.createSignUpRequest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

@WebMvcTest(MemberController::class)
class MemberControllerTest : ControllerTest() {

    @MockkBean
    private lateinit var memberService: MemberService

    @Test
    fun `회원가입을 진행한다`() {
        every { memberService.signUp(any()) } returns Unit

        mockMvc.post("/members") {
            content = objectMapper.writeValueAsString(createSignUpRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }
    }
}
