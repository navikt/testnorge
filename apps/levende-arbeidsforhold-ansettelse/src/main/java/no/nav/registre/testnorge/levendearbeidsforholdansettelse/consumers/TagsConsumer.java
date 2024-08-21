package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl.HentTagsCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.TagsDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class TagsConsumer {
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public TagsConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getPdlProxy();

        this.tokenService = tokenService;
        webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public TagsDTO hentTags(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new HentTagsCommand(webClient, token.getTokenValue(), identer).call())
                .block();
    }
}
