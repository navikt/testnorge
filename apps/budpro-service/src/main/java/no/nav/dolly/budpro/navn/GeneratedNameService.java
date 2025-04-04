package no.nav.dolly.budpro.navn;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.budpro.Consumers;
import no.nav.dolly.budpro.texas.TexasService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Service
@Slf4j
public class GeneratedNameService {

    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final TexasService texas;

    GeneratedNameService(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient,
            TexasService texas
    ) {
        serverProperties = consumers.getGenererNavnService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.texas = texas;
    }

    public String[] getNames(Long seed, int number) {
        var accessToken = tokenExchange
                .exchange(serverProperties)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Failed to get token for %s".formatted(serverProperties.getName())))
                .getTokenValue();
        var token = texas.getToken("generer-navn-service");
        var arrayOfDTOs = new MyGenererNavnCommand(webClient, token, seed, number)
                .call();
        return Arrays
                .stream(arrayOfDTOs)
                .map(name -> name.getAdjektiv() + " " + name.getSubstantiv())
                .toList()
                .toArray(new String[0]);
    }

}
