package no.nav.testnav.libs.reactivecore.web;

import io.micrometer.observation.ObservationRegistry;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.metrics.UriStrippingClientRequestObservationConvention;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@AutoConfiguration
@Slf4j
class WebClientAutoConfiguration {

    /**
     * <p>Get a {@link WebClient.Builder] with an observation registry if available.</p>
     * <p>Slated for removal once usages are refactored.</p>
     * @param context The {@link ApplicationContext}.
     * @return A {@link WebClient.Builder}.
     * @deprecated Use {@link WebClient#mutate()} instead. This will ensure that any common future modifications to the {@link WebClient} are applied.
     */
    @Deprecated(forRemoval = true)
    @Bean
    WebClient.Builder webClientBuilder(ApplicationContext context) {

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
                                                    .maxConnections(5)
                                                    .pendingAcquireMaxCount(10000)
                                                    .pendingAcquireTimeout(Duration.ofSeconds(300))
                                                    .build())
                                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                            .option(ChannelOption.SO_KEEPALIVE, true)
                                            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
                                            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
                                            .option(EpollChannelOption.TCP_KEEPCNT, 8)
                                            .responseTimeout(Duration.ofSeconds(10))
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
    WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    @Bean
    ClientRequestObservationConvention clientRequestObservationConvention() {
        return new UriStrippingClientRequestObservationConvention();
    }

}
