package no.nav.registre.sdforvalter.consumer.rs.person;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.config.Consumers;
import no.nav.registre.sdforvalter.consumer.rs.person.command.SavePersonFasteDataCommand;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.testnav.libs.dto.personservice.v1.Gruppe;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class PersonFasteDataConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PersonFasteDataConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {
        serverProperties = consumers.getTestnavPersonFasteDataService();
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public void opprett(TpsIdentListe tpsIdentListe) {
        tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> Flux.concat(
                        tpsIdentListe.getListe().stream().map(value -> opprett(value, token)).toList()
                ))
                .collectList()
                .block();
    }

    public Mono<Void> opprett(TpsIdent tpsIdent, AccessToken accessToken) {
        var gruppe = tpsIdent.getGruppe() == null ? Gruppe.ANDRE : Gruppe.valueOf(tpsIdent.getGruppe());
        return new SavePersonFasteDataCommand(
                webClient,
                accessToken.getTokenValue(),
                tpsIdent.toDTOV2(),
                gruppe
        ).call();
    }
}