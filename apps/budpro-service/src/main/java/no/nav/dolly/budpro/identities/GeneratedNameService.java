package no.nav.dolly.budpro.identities;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.reactivecore.config.WebClientConfig;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    NavnDTO[] getNames(int number) {
        var accessToken = tokenExchange
                .exchange(properties)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Failed to get token for %s".formatted(properties.getName())))
                .getTokenValue();
        return new GenererNavnCommand(webClient, accessToken, 1)
                .call();
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.generer-navn-service")
    public static class GenererNavnServiceProperties extends ServerProperties {

    }

}
