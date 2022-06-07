package no.nav.testnav.apps.importpersonservice.consumer;

import no.nav.testnav.apps.importpersonservice.consumer.command.OppdaterPersonCommand;
import no.nav.testnav.apps.importpersonservice.consumer.command.SendPersonTilPdlCommand;
import no.nav.testnav.apps.importpersonservice.consumer.request.OppdaterPersonRequest;
import no.nav.testnav.apps.importpersonservice.credentias.TestnavPdlForvalterProperties;
import no.nav.testnav.apps.importpersonservice.domain.PersonList;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

@Component
public class PdlForvalterConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PdlForvalterConsumer(
            TestnavPdlForvalterProperties serverProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serverProperties = serverProperties;
        this.tokenExchange = tokenExchange;

        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Flux<String> opprett(PersonList list) {
        Flux<OrdreResponseDTO> ordreResponseDTOFlux = tokenExchange
                .exchange(serverProperties)
                .flatMapMany(accessToken -> Flux.concat(list
                                .getList()
                                .stream()
                                .map(OppdaterPersonRequest::new)
                                .map(request -> new OppdaterPersonCommand(webClient, request, accessToken.getTokenValue()).call())
                                .collect(Collectors.toList()))
                        .flatMap(value -> new SendPersonTilPdlCommand(webClient, value, accessToken.getTokenValue()).call().flux())
                );
        return ordreResponseDTOFlux
                .map(ordre -> ordre.getHovedperson().getIdent());
    }
}
