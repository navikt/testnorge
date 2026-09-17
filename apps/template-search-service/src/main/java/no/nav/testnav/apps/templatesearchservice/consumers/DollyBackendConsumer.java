package no.nav.testnav.apps.templatesearchservice.consumers;

import no.nav.testnav.apps.templatesearchservice.config.Consumers;
import no.nav.testnav.apps.templatesearchservice.consumers.command.GetCurrentBrukerCommand;
import no.nav.testnav.apps.templatesearchservice.consumers.command.GetTeamsCommand;
import no.nav.testnav.apps.templatesearchservice.consumers.dto.CurrentBrukerDTO;
import no.nav.testnav.apps.templatesearchservice.consumers.dto.DollyTeamDTO;
import no.nav.testnav.apps.templatesearchservice.exception.DollyBackendUnavailableException;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class DollyBackendConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public DollyBackendConsumer(Consumers consumers, TokenExchange tokenExchange, WebClient webClient) {
        this.serverProperties = consumers.getDollyBackend();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient.mutate().baseUrl(serverProperties.getUrl()).build();
    }

    public Mono<String> getRepresentererTeamBrukerId() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetCurrentBrukerCommand(webClient, token.getTokenValue()).call())
                .switchIfEmpty(Mono.error(new IllegalStateException("Dolly returnerte ikke brukerdata.")))
                .onErrorMap(error -> new DollyBackendUnavailableException(
                        "Kunne ikke hente aktivt team fra Dolly.", error))
                .flatMap(DollyBackendConsumer::getRepresentererTeamBrukerId);
    }

    public Mono<List<DollyTeamDTO>> getTeams() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetTeamsCommand(webClient, token.getTokenValue()).call())
                .switchIfEmpty(Mono.error(new IllegalStateException("Dolly returnerte ikke teamdata.")))
                .onErrorMap(error -> new DollyBackendUnavailableException(
                        "Kunne ikke hente team fra Dolly.", error));
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
