package chat.teco.tecochat.config

import com.theokanning.openai.service.OpenAiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import java.time.Duration


@Configuration
class GptApiConfig(
    @Value("\${gpt.key}")
    private val key: String,

    @Value("\${gpt.url}")
    private val url: String,
) {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun httpHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(key)
        return headers
    }

    @Bean
    fun gptApiUrl(): String {
        return url
    }

    @Bean
    fun openAiService(): OpenAiService {
        return OpenAiService(key, Duration.ofSeconds(10))
    }
}


