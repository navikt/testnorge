package no.nav.testnav.libs.reactivecore.web;

import io.micrometer.observation.ObservationRegistry;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.metrics.UriStrippingClientRequestObservationConvention;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.ClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@AutoConfiguration
@Slf4j
class WebClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    WebClient webClient(@Nullable ObservationRegistry observationRegistry) {

        var builder = WebClient.builder();
        if (observationRegistry == null) {

            log.info(
                    "No {} found in context, using {} without any observation registry",
                    ObservationRegistry.class.getCanonicalName(),
                    builder.getClass().getCanonicalName()
            );

        } else {

            log.info(
                    "Using {} with observation registry {}",
                    builder.getClass().getCanonicalName(),
                    observationRegistry.getClass().getCanonicalName()
            );
            builder = builder
                    .observationConvention(new DefaultClientRequestObservationConvention())
                    .observationRegistry(observationRegistry);

        }

        return builder
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient
                                .create(ConnectionProvider
                                        .builder("Dolly Connection Pool")
                                        .maxConnections(5)
                                        .pendingAcquireMaxCount(10000)
                                        .pendingAcquireTimeout(Duration.ofSeconds(300))
                                        .build())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                .option(ChannelOption.SO_KEEPALIVE, true)
                                .option(EpollChannelOption.TCP_KEEPIDLE, 300) // Note that this is not supported on all platforms.
                                .option(EpollChannelOption.TCP_KEEPINTVL, 60) // Note that this is not supported on all platforms.
                                .option(EpollChannelOption.TCP_KEEPCNT, 8) // Note that this is not supported on all platforms.
                                .responseTimeout(Duration.ofSeconds(10))
                ))
                .build();

    }

    @Bean
    ClientRequestObservationConvention clientRequestObservationConvention() {
        return new UriStrippingClientRequestObservationConvention();
    }

}
