package no.nav.testnav.apps.tilgangservice.client.altinn.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.command.CreateOrganiasjonAccessCommand;
import no.nav.testnav.apps.tilgangservice.client.altinn.v1.command.DeleteOrganiasjonAccessCommand;
import no.nav.testnav.apps.tilgangservice.client.altinn.v1.command.GetOrganiasjonCommand;
import no.nav.testnav.apps.tilgangservice.client.altinn.v1.command.GetPersonAccessCommand;
import no.nav.testnav.apps.tilgangservice.client.altinn.v1.command.GetRightsCommand;
import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.AccessDTO;
import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.RightDTO;
import no.nav.testnav.apps.tilgangservice.client.maskinporten.v1.MaskinportenClient;
import no.nav.testnav.apps.tilgangservice.config.AltinnConfig;
import no.nav.testnav.apps.tilgangservice.domain.Access;
import no.nav.testnav.apps.tilgangservice.domain.Organisajon;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserIdAction;

@Component
public class AltinnClient {

    private final WebClient webClient;
    private final AltinnConfig altinnConfig;
    private final MaskinportenClient maskinportenClient;
    private final GetAuthenticatedUserIdAction getAuthenticatedUserId;

    public AltinnClient(
            AltinnConfig altinnConfig,
            MaskinportenClient maskinportenClient,
            ObjectMapper objectMapper,
            GetAuthenticatedUserIdAction getAuthenticatedUserId
    ) {
        this.altinnConfig = altinnConfig;
        this.maskinportenClient = maskinportenClient;
        this.getAuthenticatedUserId = getAuthenticatedUserId;
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

    public Flux<Access> getAccess() {
        return getAuthenticatedUserId
                .call()
                .flatMapMany(userId -> maskinportenClient
                        .getAccessToken()
                        .flatMap(accessToken -> new GetPersonAccessCommand(
                                        webClient,
                                        accessToken.value(),
                                        userId,
                                        altinnConfig.getCode(),
                                        altinnConfig.getEdition(),
                                        altinnConfig.getApiKey()
                                ).call()
                        ).map(accesses -> Arrays
                                .stream(accesses)
                                .filter(AccessDTO::isActive)
                                .map(Access::new)
                                .collect(Collectors.toList())
                        ).flatMapIterable(list -> list)
                );
    }

    public Mono<Access> getAccess(String organiasjonsnummer) {
        return getAccess()
                .filter(value -> value.getOrgnisajonsnummer().equals(organiasjonsnummer))
                .next();
    }


    public Flux<Void> delete(String organiasjonsnummer) {
        return getRights()
                .filter(value -> value.reportee().equals(organiasjonsnummer))
                .flatMap(value -> maskinportenClient
                        .getAccessToken()
                        .flatMap(accessToken -> new DeleteOrganiasjonAccessCommand(
                                webClient,
                                accessToken.value(),
                                altinnConfig.getApiKey(),
                                value.id()
                        ).call())
                );
    }

    public Mono<Organisajon> create(String organiasjonsnummer, LocalDateTime gyldigTil) {
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
                .flatMap(accessToken -> new CreateOrganiasjonAccessCommand(
                                webClient,
                                accessToken.value(),
                                altinnConfig.getApiKey(),
                                readRight
                        ).call()
                ).flatMap(right -> maskinportenClient
                        .getAccessToken()
                        .flatMap(accessToken -> new GetOrganiasjonCommand(
                                webClient,
                                accessToken.value(),
                                right.reportee(),
                                altinnConfig.getApiKey()
                        ).call())
                        .map(value -> new Organisajon(value, right))
                );
    }


    public Flux<Organisajon> getOrganiasjoner() {
        return getRights()
                .map(right -> maskinportenClient
                        .getAccessToken()
                        .flatMap(accessToken -> new GetOrganiasjonCommand(
                                webClient,
                                accessToken.value(),
                                right.reportee(),
                                altinnConfig.getApiKey()
                        ).call())
                        .map(value -> new Organisajon(value, right))
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
