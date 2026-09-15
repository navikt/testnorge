package no.nav.testnav.apps.brukerservice.consumer;

import no.nav.testnav.apps.brukerservice.config.Consumers;
import no.nav.testnav.apps.brukerservice.consumer.command.GetCurrentBrukerCommand;
import no.nav.testnav.apps.brukerservice.consumer.dto.CurrentBrukerDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class DollyBackendConsumer {

    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public DollyBackendConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        this.serviceProperties = consumers.getDollyBackend();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Mono<String> getRepresentererTeamBrukerId() {

        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetCurrentBrukerCommand(webClient, accessToken.getTokenValue()).call())
                .switchIfEmpty(Mono.error(new AccessDeniedException("Dolly returnerte ikke brukerdata.")))
                .flatMap(DollyBackendConsumer::getRepresentererTeamBrukerId);
    }

    private static Mono<String> getRepresentererTeamBrukerId(CurrentBrukerDTO response) {

        if (isNull(response.representererTeam())) {
            return Mono.empty();
        }
        if (isBlank(response.representererTeam().brukerId())) {
            return Mono.error(new AccessDeniedException("Dolly returnerte ugyldig teamkontekst."));
        }
        return Mono.just(response.representererTeam().brukerId().trim());
    }
}
