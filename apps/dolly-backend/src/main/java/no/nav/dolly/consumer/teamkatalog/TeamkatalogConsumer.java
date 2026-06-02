package no.nav.dolly.consumer.teamkatalog;

import no.nav.dolly.config.Consumers;
import no.nav.dolly.consumer.teamkatalog.command.TeamkatalogGetCommand;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class TeamkatalogConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public TeamkatalogConsumer(
            Consumers consumers,
            TokenExchange tokenService,
            WebClient webClient) {

        serverProperties = consumers.getTeamkatalog();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "saf_getDokument" })
    public Flux<TeamkatalogDTO> getTeamForEpost(List<String> epost) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new TeamkatalogGetCommand(webClient,
                       epost, token.getTokenValue()).call());
    }
}
