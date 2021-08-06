package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer.command.GetOrgnummerCommand;
import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.credentials.OrgnummerServiceProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

@Component
public class OrgnummerConsumer {

    private final WebClient webClient;
    private final OrgnummerServiceProperties properties;
    private final AccessTokenService accessTokenService;

    public OrgnummerConsumer(OrgnummerServiceProperties properties, AccessTokenService accessTokenService) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public String getOrgnummer() {
        var tokenValue = accessTokenService.generateToken(properties).block().getTokenValue();
        return new GetOrgnummerCommand(webClient, tokenValue, 1).call().stream().findFirst().orElseThrow();
    }

}