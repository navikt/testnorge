package no.nav.dolly.budpro.navn;

import no.nav.dolly.budpro.Consumers;
import no.nav.testnav.libs.reactivecore.config.WebClientConfig;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
@Import(WebClientConfig.class)
public class GeneratedNameService {

    private final Mono<AccessToken> accessToken;
    private final WebClient webClient;

    GeneratedNameService(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder
    ) {
        var consumer = consumers.getGenererNavnService();
        accessToken = tokenExchange.exchange(consumer);
        webClient = webClientBuilder
                .baseUrl(consumer.getUrl())
                .build();
    }

    public String[] getNames(Long seed, int number) {
        var names = new CustomGenererNavnCommand(webClient, accessToken, seed, number).call();
        return Arrays
                .stream(names)
                .map(name -> name.getAdjektiv() + " " + name.getSubstantiv())
                .toList()
                .toArray(new String[0]);
    }

}
