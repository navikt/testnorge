package no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.command.CreateOrganisasjonAccessCommand;
import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.command.DeleteOrganisasjonAccessCommand;
import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.command.GetOrganisasjonCommand;
import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.command.GetRightsCommand;
import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.dto.RightDTO;
import no.nav.testnav.apps.organisasjontilgangservice.client.maskinporten.v1.MaskinportenClient;
import no.nav.testnav.apps.organisasjontilgangservice.config.AltinnConfig;
import no.nav.testnav.apps.organisasjontilgangservice.domain.Organisasjon;

@Component
public class AltinnClient {

    private final WebClient webClient;
    private final AltinnConfig altinnConfig;
    private final MaskinportenClient maskinportenClient;

    public AltinnClient(
            AltinnConfig altinnConfig,
            MaskinportenClient maskinportenClient,
            ObjectMapper objectMapper
    ) {
        this.altinnConfig = altinnConfig;
        this.maskinportenClient = maskinportenClient;
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
                .build();
    }

    public Flux<Void> delete(String organiasjonsnummer) {
        return getRights()
                .filter(value -> value.reportee().equals(organiasjonsnummer))
                .flatMap(value -> maskinportenClient
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
        return maskinportenClient
                .getAccessToken()
                .flatMap(accessToken -> new CreateOrganisasjonAccessCommand(
                                webClient,
                                accessToken.value(),
                                altinnConfig.getApiKey(),
                                readRight
                        ).call()
                ).flatMap(right -> maskinportenClient
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
                .map(right -> maskinportenClient
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
        return maskinportenClient
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
