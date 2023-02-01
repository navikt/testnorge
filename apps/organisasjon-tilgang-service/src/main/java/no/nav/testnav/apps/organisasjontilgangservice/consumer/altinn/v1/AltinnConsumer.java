package no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.apps.organisasjontilgangservice.config.AltinnConfig;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.command.CreateOrganisasjonAccessCommand;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.command.DeleteOrganisasjonAccessCommand;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.command.GetOrganisasjonCommand;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.command.GetRightsCommand;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.dto.DeleteStatus;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.dto.RightDTO;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.maskinporten.v1.MaskinportenConsumer;
import no.nav.testnav.apps.organisasjontilgangservice.domain.Organisasjon;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class AltinnConsumer {

    private final WebClient webClient;
    private final AltinnConfig altinnConfig;
    private final MaskinportenConsumer maskinportenConsumer;

    public AltinnConsumer(
            AltinnConfig altinnConfig,
            MaskinportenConsumer maskinportenConsumer,
            ObjectMapper objectMapper,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.altinnConfig = altinnConfig;
        this.maskinportenConsumer = maskinportenConsumer;
        this.webClient = WebClient
                .builder()
                .baseUrl(altinnConfig.getUrl())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                })
                .filter(metricsWebClientFilterFunction)
                .build();
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
