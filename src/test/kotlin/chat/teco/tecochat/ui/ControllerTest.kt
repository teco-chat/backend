package chat.teco.tecochat.ui

import chat.teco.tecochat.security.Auth
import chat.teco.tecochat.security.AuthArgumentResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.core.MethodParameter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.NativeWebRequest

@AutoConfigureMockMvc
@ExtendWith(MockKExtension::class)
abstract class ControllerTest {

    @MockkBean
    private lateinit var authArgumentResolver: AuthArgumentResolver

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(
        webApplicationContext: WebApplicationContext
    ) {
        authArgumentResolver.also {
            slot<MethodParameter>().also { slot ->
                every { it.supportsParameter(capture(slot)) } answers {
                    slot.captured.hasParameterAnnotation(Auth::class.java)
                }
            }
            slot<NativeWebRequest>().also { slot ->
                every { it.resolveArgument(any(), any(), capture(slot), any()) } returns 1L
            }
        }
    }
}
