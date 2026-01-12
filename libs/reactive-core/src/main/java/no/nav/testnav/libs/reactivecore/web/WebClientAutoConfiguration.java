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
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@AutoConfiguration
@Slf4j
class WebClientAutoConfiguration {

    private static JsonMapper createDefaultJsonMapper() {
        return JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    WebClient webClient(ObservationRegistry observationRegistry, ObjectMapper objectMapper) {

        JsonMapper jsonMapper;
        if (objectMapper instanceof JsonMapper jm) {
            jsonMapper = jm;
            log.info("WebClientAutoConfiguration: Using existing JsonMapper: {}", objectMapper.getClass().getName());
        } else {
            jsonMapper = createDefaultJsonMapper();
            log.info("WebClientAutoConfiguration: ObjectMapper is not JsonMapper ({}), creating new JsonMapper with default configuration", 
                    objectMapper != null ? objectMapper.getClass().getName() : "null");
        }

        var exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                    configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                    configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                })
                .build();

        var builder = WebClient.builder()
                .exchangeStrategies(exchangeStrategies);

        log.info(
                "Using {} with observation registry {}",
                builder.getClass().getCanonicalName(),
                observationRegistry.getClass().getCanonicalName()
        );
        builder = builder
                .observationConvention(new DefaultClientRequestObservationConvention())
                .observationRegistry(observationRegistry);


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
                                .option(EpollChannelOption.TCP_KEEPIDLE, 300)
                                .option(EpollChannelOption.TCP_KEEPINTVL, 60)
                                .option(EpollChannelOption.TCP_KEEPCNT, 8)
                                .responseTimeout(Duration.ofSeconds(10))
                ))
                .build();

    }

    @Bean
    ClientRequestObservationConvention clientRequestObservationConvention() {
        return new UriStrippingClientRequestObservationConvention();
    }

}
