package no.nav.testnav.libs.reactivecore.config;

import no.nav.testnav.libs.reactivecore.filter.RequestLogger;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@Import({
        RequestLogger.class,
        WebClientLogger.class,
        ApplicationProperties.class
})
@Configuration
public class CoreConfig {

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
