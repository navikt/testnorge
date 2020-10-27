package no.nav.registre.testnorge.identservice.config;

import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ComptestConfig {

    @Bean
    @Primary
    public MessageQueueConsumer defaultMessageQueueConsumer() {
        return Mockito.mock(MessageQueueConsumer.class);
    }

}
