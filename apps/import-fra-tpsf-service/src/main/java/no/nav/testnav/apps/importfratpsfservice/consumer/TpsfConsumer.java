package no.nav.testnav.apps.importfratpsfservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.config.credentials.TpsfProperties;
import no.nav.testnav.apps.importfratpsfservice.consumer.command.TpsfGetSkdMeldingerCommand;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TpsfConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public TpsfConsumer(TokenExchange tokenExchange, TpsfProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Flux<SkdEndringsmelding> getSkdMeldinger(Long gruppeId, Long sidenummer) {

        return tokenExchange.exchange(properties).flatMapMany(
                token -> new TpsfGetSkdMeldingerCommand(webClient, gruppeId, sidenummer, token.getTokenValue()).call());
    }
}
