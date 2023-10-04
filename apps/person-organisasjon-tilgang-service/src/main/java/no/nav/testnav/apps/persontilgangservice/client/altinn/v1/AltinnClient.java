package no.nav.testnav.apps.persontilgangservice.client.altinn.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.apps.persontilgangservice.client.altinn.v1.command.GetPersonAccessCommand;
import no.nav.testnav.apps.persontilgangservice.client.altinn.v1.dto.AccessDTO;
import no.nav.testnav.apps.persontilgangservice.client.maskinporten.v1.MaskinportenClient;
import no.nav.testnav.apps.persontilgangservice.config.AltinnConfig;
import no.nav.testnav.apps.persontilgangservice.domain.Access;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
public class AltinnClient {

    private final WebClient webClient;
    private final AltinnConfig altinnConfig;
    private final MaskinportenClient maskinportenClient;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    public AltinnClient(
            AltinnConfig altinnConfig,
            MaskinportenClient maskinportenClient,
            ObjectMapper objectMapper,
            GetAuthenticatedUserId getAuthenticatedUserId
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
                                .toList()
                        ).flatMapIterable(list -> list)
                );
    }

    public Mono<Access> getAccess(String organiasjonsnummer) {
        return getAccess()
                .filter(value -> value.getOrganisajonsnummer().equals(organiasjonsnummer))
                .next();
    }

}
