package chat.teco.tecochat.common.config;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockPublisherConfiguration {

    @Primary
    @Bean
    public ApplicationEventPublisher applicationEventPublisher() {
        return mock(ApplicationEventPublisher.class);
    }
}
