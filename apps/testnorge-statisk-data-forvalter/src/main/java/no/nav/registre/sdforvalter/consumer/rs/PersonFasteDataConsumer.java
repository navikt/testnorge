package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.config.credentials.PersonFasteDataServiceProperties;
import no.nav.registre.sdforvalter.consumer.rs.commnad.SavePersonFasteDataCommand;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.testnav.libs.dto.personservice.v1.Gruppe;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class PersonFasteDataConsumer {
    private final WebClient webClient;
    private final PersonFasteDataServiceProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PersonFasteDataConsumer(
            PersonFasteDataServiceProperties serverProperties,
            TokenExchange tokenExchange
    ) {
        this.serverProperties = serverProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public void opprett(TpsIdentListe tpsIdentListe) {
        tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> Flux.concat(
                        tpsIdentListe.getListe().stream().map(value -> opprett(value, token)).collect(Collectors.toList())
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
