package no.nav.registre.tp.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.consumer.rs.command.GetSyntTpYtelserCommand;
import no.nav.registre.tp.consumer.rs.credential.SyntTpProperties;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;

import no.nav.registre.tp.database.models.TYtelse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class TpSyntConsumer {

    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public TpSyntConsumer(
            SyntTpProperties syntTpProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = syntTpProperties;
        this.tokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntTpProperties.getUrl())
                .build();
    }

    @Timed(value = "tp.resource.latency", extraTags = { "operation", "synt" })
    public List<TYtelse> getSyntYtelser(
            int numToGenerate
    ) {
        var token = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();
        return new GetSyntTpYtelserCommand(numToGenerate, token, webClient).call();
    }
}
