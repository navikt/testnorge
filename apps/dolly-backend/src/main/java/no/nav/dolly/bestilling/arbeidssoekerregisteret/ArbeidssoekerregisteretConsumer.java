package no.nav.dolly.bestilling.arbeidssoekerregisteret;

import no.nav.dolly.bestilling.arbeidssoekerregisteret.command.LagreTilArbeidsoekerregisteret;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssokerregisteretRequest;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssokerregisteretResponse;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ArbeidssoekerregisteretConsumer {

   private ServerProperties serverProperties;
   private WebClient webClient;
   private TokenExchange tokenExchange;

    public ArbeidssoekerregisteretConsumer(Consumers consumers, WebClient.Builder webClient, TokenExchange tokenExchange) {
         this.serverProperties = consumers.getArbeidssoekerregisteretProxy();
         this.webClient = webClient
                 .baseUrl(serverProperties.getUrl())
                 .build();
         this.tokenExchange = tokenExchange;
    }

    public Mono<ArbeidssokerregisteretResponse> postArbeidssokerregisteret(ArbeidssokerregisteretRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new LagreTilArbeidsoekerregisteret(webClient, request, token.getTokenValue()).call());
    }
}
