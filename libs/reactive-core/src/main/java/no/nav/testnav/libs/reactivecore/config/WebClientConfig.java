package no.nav.testnav.libs.reactivecore.config;

import io.micrometer.observation.ObservationRegistry;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.metrics.UriStrippingClientRequestObservationConvention;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

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
                    .observationConvention(new DefaultClientRequestObservationConvention())
                    .observationRegistry(observationRegistry)
                    .clientConnector(
                            new ReactorClientHttpConnector(
                                    HttpClient
                                            .create(ConnectionProvider.builder("Testnorge connection pool")
                                                    .maxConnections(10)
                                                    .pendingAcquireMaxCount(3000)
                                                    .build())
                                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                            .option(ChannelOption.SO_KEEPALIVE, true)
                                            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
                                            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
                                            .option(EpollChannelOption.TCP_KEEPCNT, 8)
                                            .responseTimeout(Duration.ofSeconds(30))
                            ));

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

    @Bean
    public ClientRequestObservationConvention clientRequestObservationConvention() {
        return new UriStrippingClientRequestObservationConvention();
    }

}
