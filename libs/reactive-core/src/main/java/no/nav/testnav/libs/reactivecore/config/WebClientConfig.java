package no.nav.testnav.libs.reactivecore.config;

import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder(ApplicationContext context) {

        try {

            var observationRegistry = context.getBean(ObservationRegistry.class);
            log.info(
                    "Using {} with observation registry {}",
                    WebClient.Builder.class.getCanonicalName(),
                    observationRegistry.getClass().getCanonicalName()
            );
            return WebClient
                    .builder()
                    .observationConvention(new DefaultClientRequestObservionConvention())
                    .observationRegistry(observationRegistry)
                    .clientConnector(
                            new ReactorClientHttpConnector(
                                    HttpClient
                                            .create()
                                            .responseTimeout(Duration.ofSeconds(30))
                                            .resolver(spec -> spec.queryTimeout(Duration.ofSeconds(30)))));

        } catch (NoSuchBeanDefinitionException e) {

            log.info(
                    "No {} found in context, using {} without any observation registry",
                    ObservationRegistry.class.getCanonicalName(),
                    WebClient.Builder.class.getCanonicalName()
            );
            return WebClient.builder();

        }
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
}
