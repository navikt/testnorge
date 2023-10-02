package no.nav.dolly.budpro.identities;

import no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand;
import no.nav.testnav.libs.reactivecore.config.WebClientConfig;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Service
@Import(WebClientConfig.class)
public class GeneratedNameService {

    private final GenererNavnServiceProperties properties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;

    GeneratedNameService(GenererNavnServiceProperties properties, TokenExchange tokenExchange, WebClient.Builder webClientBuilder) {
        this.properties = properties;
        this.tokenExchange = tokenExchange;
        this.webClient = webClientBuilder
                .baseUrl(properties.getUrl())
                .build();
    }

    public String[] getNames(Long seed, int number) {
        var accessToken = tokenExchange
                .exchange(properties)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Failed to get token for %s".formatted(properties.getName())))
                .getTokenValue();
        var arrayOfDTOs = new GenererNavnCommand(webClient, accessToken, seed, number)
                .call();
        return Arrays
                .stream(arrayOfDTOs)
                .map(name -> name.getAdjektiv() + " " + name.getSubstantiv())
                .toList()
                .toArray(new String[0]);
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.generer-navn-service")
    public static class GenererNavnServiceProperties extends ServerProperties {

    }

}
