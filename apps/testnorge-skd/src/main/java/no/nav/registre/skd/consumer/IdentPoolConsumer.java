package no.nav.registre.skd.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import no.nav.registre.skd.consumer.command.identpool.GetNavnCommand;
import no.nav.registre.skd.consumer.command.identpool.PostFrigjoerLedigeIdenterCommand;
import no.nav.registre.skd.consumer.command.identpool.PostHentIdenterCommand;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
import no.nav.registre.skd.consumer.response.Navn;

@Slf4j
@Component
public class IdentPoolConsumer {

    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<Navn>> RESPONSE_TYPE_NAVN = new ParameterizedTypeReference<>() {
    };

    public IdentPoolConsumer(@Value("${ident-pool.rest-api.url}") String serverUrl) {
        var objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();

        this.webClient = WebClient.builder().baseUrl(serverUrl)
                .exchangeStrategies(jacksonStrategy)
                .build();

    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "identpool" })
    public List<String> hentNyeIdenter(HentIdenterRequest hentIdenterRequest) {
        return new PostHentIdenterCommand(Objects.requireNonNull(hentIdenterRequest), webClient).call();
    }

    public Navn hentNavn() {
        var navn = new GetNavnCommand(webClient).call();

        if (navn == null) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Kunne ikke hente navn");
        }
        if (navn.size() != 1) {
            log.error("Fikk feil antall navn - størrelse: {}", navn.size());
        }
        return navn.get(0);
    }

    public List<String> frigjoerLedigeIdenter(List<String> identer) {
        return new PostFrigjoerLedigeIdenterCommand(identer, webClient).call();
    }
}
