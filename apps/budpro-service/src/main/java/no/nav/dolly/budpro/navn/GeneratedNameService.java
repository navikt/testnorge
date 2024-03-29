package no.nav.dolly.budpro.navn;

import no.nav.dolly.budpro.Consumers;
import no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand;
import no.nav.testnav.libs.reactivecore.config.WebClientConfig;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Service
@Import(WebClientConfig.class)
public class GeneratedNameService {

    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;

    GeneratedNameService(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder
    ) {
        serverProperties = consumers.getGenererNavnService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public String[] getNames(Long seed, int number) {
        var accessToken = tokenExchange
                .exchange(serverProperties)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Failed to get token for %s".formatted(serverProperties.getName())))
                .getTokenValue();
        var arrayOfDTOs = new GenererNavnCommand(webClient, accessToken, seed, number)
                .call();
        return Arrays
                .stream(arrayOfDTOs)
                .map(name -> name.getAdjektiv() + " " + name.getSubstantiv())
                .toList()
                .toArray(new String[0]);
    }

}
