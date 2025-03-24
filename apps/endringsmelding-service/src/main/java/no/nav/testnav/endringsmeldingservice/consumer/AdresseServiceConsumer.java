package no.nav.testnav.endringsmeldingservice.consumer;

import no.nav.testnav.endringsmeldingservice.config.Consumers;
import no.nav.testnav.endringsmeldingservice.consumer.command.VegadresseServiceCommand;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AdresseServiceConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public AdresseServiceConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getAdresseService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<List<VegadresseDTO>> getVegadresse() {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new VegadresseServiceCommand(webClient, token.getTokenValue()).call())
                .collectList();

    }
}
