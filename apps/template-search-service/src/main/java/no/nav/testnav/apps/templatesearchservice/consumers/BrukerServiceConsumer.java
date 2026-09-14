package no.nav.testnav.apps.templatesearchservice.consumers;

import no.nav.testnav.apps.templatesearchservice.config.Consumers;
import no.nav.testnav.apps.templatesearchservice.consumers.command.GetUsersInSameOrgCommand;
import no.nav.testnav.apps.templatesearchservice.consumers.dto.BrukereDTO;
import no.nav.testnav.apps.templatesearchservice.exception.BrukerServiceUnavailableException;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BrukerServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public BrukerServiceConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getBrukerService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<BrukereDTO> getKollegaerIOrganisasjon(String brukerId) {
        return tokenExchange.exchange(serverProperties)
                .onErrorMap(error -> new BrukerServiceUnavailableException(
                        "Kunne ikke utveksle token mot bruker-service.",
                        error))
                .flatMap(token -> new GetUsersInSameOrgCommand(
                        webClient,
                        brukerId,
                        token.getTokenValue()).call());
    }
}
