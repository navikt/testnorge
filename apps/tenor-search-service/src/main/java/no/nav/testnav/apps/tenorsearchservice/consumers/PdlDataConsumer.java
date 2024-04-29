package no.nav.testnav.apps.tenorsearchservice.consumers;

import no.nav.testnav.apps.tenorsearchservice.config.Consumers;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.TagsGetCommand;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static java.util.Objects.nonNull;

@Service
public class PdlDataConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public PdlDataConsumer(TokenExchange tokenExchange, Consumers serverProperties) {
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getPdlTestdata().getUrl())
                .build();
        this.tokenExchange = tokenExchange;
        this.serverProperties = serverProperties.getPdlTestdata();
    }

    public Mono<DollyTagDTO> hasPdlDollyTag(String ident) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new TagsGetCommand(webClient, ident, token.getTokenValue()).call())
                .map(resultat -> DollyTagDTO.builder()
                        .ident(ident)
                        .hasDollyTag(nonNull(resultat) && Arrays.asList(resultat).contains("DOLLY"))
                        .build());
    }
}
