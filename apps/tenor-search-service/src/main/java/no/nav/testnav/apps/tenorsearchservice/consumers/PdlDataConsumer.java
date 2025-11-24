package no.nav.testnav.apps.tenorsearchservice.consumers;

import no.nav.testnav.apps.tenorsearchservice.config.Consumers;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.TagsGetCommand;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagsDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PdlDataConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public PdlDataConsumer(
            TokenExchange tokenExchange,
            Consumers serverProperties,
            WebClient webClient
    ) {
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getPdlTestdata().getUrl())
                .build();
        this.tokenExchange = tokenExchange;
        this.serverProperties = serverProperties.getPdlTestdata();
    }

    public Mono<DollyTagsDTO> hasPdlDollyTag(List<String> identer) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new TagsGetCommand(webClient, identer, token.getTokenValue()).call());
    }

}
