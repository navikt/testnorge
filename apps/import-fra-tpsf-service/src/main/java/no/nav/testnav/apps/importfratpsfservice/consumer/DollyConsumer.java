package no.nav.testnav.apps.importfratpsfservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.config.credentials.TpsfProperties;
import no.nav.testnav.apps.importfratpsfservice.consumer.command.DollyPutIdentCommand;
import no.nav.testnav.apps.importfratpsfservice.consumer.command.TpsfGetSkdMeldingerCommand;
import no.nav.testnav.apps.importfratpsfservice.consumer.command.TpsfGetSkdMeldingsgrupperCommand;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmeldingGruppe;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master.*;

@Slf4j
@Service
public class DollyConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public DollyConsumer(TokenExchange tokenExchange, TpsfProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Mono<Void> putGrupperIdent(Long gruppeId, String ident, DbVersjonDTO.Master master) {

        return tokenExchange.exchange(properties).flatMap(
                token -> new DollyPutIdentCommand(webClient, gruppeId, ident, master, token.getTokenValue()).call());
    }
}
