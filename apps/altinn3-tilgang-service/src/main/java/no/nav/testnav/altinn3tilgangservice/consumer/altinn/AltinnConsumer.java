package no.nav.testnav.altinn3tilgangservice.consumer.altinn;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.config.AltinnConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.CreateOrganisasjonAccessCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.DeleteOrganisasjonAccessCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.GetOrganisasjonCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.command.GetRightsCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.DeleteStatus;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.RightDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.MaskinportenConsumer;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AltinnConsumer {

    private final WebClient webClient;
    private final AltinnConfig altinnConfig;
    private final MaskinportenConsumer maskinportenConsumer;

    public AltinnConsumer(
            AltinnConfig altinnConfig,
            MaskinportenConsumer maskinportenConsumer,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {

        this.altinnConfig = altinnConfig;
        this.maskinportenConsumer = maskinportenConsumer;
        this.webClient = webClientBuilder
                .baseUrl(altinnConfig.getUrl())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                })
                .filters(exchangeFilterFunctions ->
                        exchangeFilterFunctions.add(logRequest()))
                .build();
    }

    private ExchangeFilterFunction logRequest() {

        return (clientRequest, next) -> {
            var buffer = new StringBuilder(250)
                    .append("Request: ")
                    .append(clientRequest.method())
                    .append(' ')
                    .append(clientRequest.url())
                    .append(System.lineSeparator());

            clientRequest.headers()
                    .forEach((name, values) -> values
                            .forEach(value -> buffer.append('\t')
                                    .append(name)
                                    .append('=')
                                    .append(value.contains("Bearer ") ? "Bearer token" : value)
                                    .append(System.lineSeparator())));
            log.trace(buffer.substring(0, buffer.length() - 1));
            return next.exchange(clientRequest);
        };
    }


    public Flux<DeleteStatus> delete(String organiasjonsnummer) {
        return getRights()
                .filter(value -> value.reportee().equals(organiasjonsnummer))
                .flatMap(value -> maskinportenConsumer
                        .getAccessToken()
                        .flatMap(accessToken -> new DeleteOrganisasjonAccessCommand(
                                webClient,
                                accessToken.value(),
                                altinnConfig.getApiKey(),
                                value.id()
                        ).call())
                );
    }

    public Mono<Organisasjon> create(String organiasjonsnummer, LocalDateTime gyldigTil) {

        var readRight = new RightDTO(
                null,
                organiasjonsnummer,
                altinnConfig.getCode(),
                altinnConfig.getEdition(),
                "Read",
                gyldigTil
        );
        return maskinportenConsumer
                .getAccessToken()
                .flatMap(accessToken -> new CreateOrganisasjonAccessCommand(
                                webClient,
                                accessToken.value(),
                                altinnConfig.getApiKey(),
                                readRight
                        ).call()
                ).flatMap(right -> maskinportenConsumer
                        .getAccessToken()
                        .flatMap(accessToken -> new GetOrganisasjonCommand(
                                webClient,
                                accessToken.value(),
                                right.reportee(),
                                altinnConfig.getApiKey()
                        ).call())
                        .map(value -> new Organisasjon(value, right))
                );
    }

    public Flux<Organisasjon> getOrganisasjoner() {

        return getRights()
                .map(right -> maskinportenConsumer
                        .getAccessToken()
                        .flatMap(accessToken -> new GetOrganisasjonCommand(
                                webClient,
                                accessToken.value(),
                                right.reportee(),
                                altinnConfig.getApiKey()
                        ).call())
                        .map(value -> new Organisasjon(value, right))
                ).collectList()
                .flatMapMany(Flux::concat);
    }

    private Flux<RightDTO> getRights() {
        return maskinportenConsumer
                .getAccessToken()
                .flatMapMany(accessToken -> new GetRightsCommand(
                                webClient,
                                accessToken.value(),
                                altinnConfig.getCode(),
                                altinnConfig.getEdition(),
                                altinnConfig.getApiKey()
                        ).call()
                );
    }

}
