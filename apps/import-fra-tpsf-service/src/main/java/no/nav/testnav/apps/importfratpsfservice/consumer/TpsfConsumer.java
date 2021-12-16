package no.nav.testnav.apps.importfratpsfservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.config.credentials.TpsfProperties;
import no.nav.testnav.apps.importfratpsfservice.consumer.command.TpsfGetSkdMeldingerCommand;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TpsfConsumer {

    private final WebClient webClient;
    private final no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange tokenExchange;
    private final no.nav.testnav.libs.securitycore.domain.ServerProperties properties;

    public TpsfConsumer(TokenExchange tokenExchange, TpsfProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Mono<SkdEndringsmelding[]> getSkdMeldinger(Long gruppeId, Long sidenummer) {

        return tokenExchange.exchange(properties).flatMap(
                token -> new TpsfGetSkdMeldingerCommand(webClient, gruppeId, sidenummer, token.getTokenValue()).call());
    }
}
