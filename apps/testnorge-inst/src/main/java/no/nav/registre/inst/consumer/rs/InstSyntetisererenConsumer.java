package no.nav.registre.inst.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.consumer.rs.command.GetSyntInstMeldingerCommand;
import no.nav.registre.inst.consumer.rs.credential.SyntInstGcpProperties;
import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@Slf4j
public class InstSyntetisererenConsumer {

    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public InstSyntetisererenConsumer(
            SyntInstGcpProperties syntInstGcpProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = syntInstGcpProperties;
        this.tokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntInstGcpProperties.getUrl())
                .build();
    }


    @Timed(value = "inst.resource.latency", extraTags = {"operation", "inst-syntetisereren"})
    public List<InstitusjonsoppholdV2> hentInstMeldingerFromSyntRest(int numToGenerate) {
        var accessToken = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();
        return new GetSyntInstMeldingerCommand(numToGenerate, accessToken, webClient).call();
    }
}
