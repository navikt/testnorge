package no.nav.testnav.apps.brukerservice.consumer;

import no.nav.testnav.apps.brukerservice.config.Consumers;
import no.nav.testnav.apps.brukerservice.consumer.command.GetCurrentBrukerCommand;
import no.nav.testnav.apps.brukerservice.consumer.dto.CurrentBrukerDTO;
import no.nav.testnav.apps.brukerservice.security.GetAuthenticatedClientName;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;
import static no.nav.testnav.libs.securitycore.config.UserConstant.TEAM_BRUKER_ID_DEV_PREFIX;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class DollyBackendConsumer {

    private static final String LOCAL_DOLLY_CLIENT = "team-dolly-local";

    private final GetAuthenticatedClientName getAuthenticatedClientName;
    private final WebClient webClient;
    private final ServerProperties dollyBackendProperties;
    private final ServerProperties dollyBackendDevProperties;
    private final TokenExchange tokenExchange;

    public DollyBackendConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            GetAuthenticatedClientName getAuthenticatedClientName,
            WebClient webClient
    ) {
        this.dollyBackendProperties = consumers.getDollyBackend();
        this.dollyBackendDevProperties = consumers.getDollyBackendDev();
        this.tokenExchange = tokenExchange;
        this.getAuthenticatedClientName = getAuthenticatedClientName;
        this.webClient = webClient;
    }

    public Mono<String> getRepresentererTeamBrukerId() {

        return getAuthenticatedClientName.call()
                .map(LOCAL_DOLLY_CLIENT::equals)
                .defaultIfEmpty(false)
                .flatMap(useDevBackend -> getRepresentererTeamBrukerId(
                        useDevBackend ? dollyBackendDevProperties : dollyBackendProperties,
                        useDevBackend));
    }

    private Mono<String> getRepresentererTeamBrukerId(
            ServerProperties serviceProperties,
            boolean useDevBackend
    ) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetCurrentBrukerCommand(
                        webClient.mutate().baseUrl(serviceProperties.getUrl()).build(),
                        accessToken.getTokenValue()).call())
                .switchIfEmpty(Mono.error(new AccessDeniedException("Dolly returnerte ikke brukerdata.")))
                .flatMap(DollyBackendConsumer::getRepresentererTeamBrukerId)
                .map(teamBrukerId -> useDevBackend
                        ? TEAM_BRUKER_ID_DEV_PREFIX + teamBrukerId
                        : teamBrukerId);
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
