package no.nav.registre.endringsmeldinger.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.endringsmeldinger.consumer.rs.command.GetSyntNavMeldingerCommand;
import no.nav.registre.endringsmeldinger.consumer.rs.credential.SyntNavProperties;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;

import java.util.List;

@Slf4j
@Component
public class SyntNavConsumer {

    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntNavConsumer(
            SyntNavProperties syntProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = syntProperties;
        this.tokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntProperties.getUrl())
                .build();
    }

    public List<Document> getSyntetiserteNavEndringsmeldinger(
            String endringskode,
            int antallMeldinger
    ) {
        var token = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();
        return new GetSyntNavMeldingerCommand(endringskode, antallMeldinger, token, webClient).call();
    }
}
