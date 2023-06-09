package chat.teco.tecochat.common.config;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GptApiConfig {

    @Value("${gpt.key}")
    private String key;

    @Value("${gpt.url}")
    private String url;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setBearerAuth(key);
        return headers;
    }

    @Bean
    public String gptApiUrl() {
        return url;
    }

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(key, Duration.ofSeconds(10));
    }
}
