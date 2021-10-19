package no.nav.registre.tss.consumer.rs;

import no.nav.registre.tss.consumer.rs.command.PostSyntTssMeldingerCommand;
import no.nav.registre.tss.consumer.rs.response.TssMessage;
import no.nav.registre.tss.domain.Samhandler;
import no.nav.registre.tss.consumer.rs.credential.SyntTssGcpProperties;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class TssSyntetisererenConsumer {

    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public TssSyntetisererenConsumer(
            SyntTssGcpProperties syntTssGcpProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = syntTssGcpProperties;
        this.tokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntTssGcpProperties.getUrl())
                .build();
    }

    public Map<String, List<TssMessage>> hentSyntetiskeTssRutiner(List<Samhandler> samhandlere) {
        var accessToken = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();
        return new PostSyntTssMeldingerCommand(samhandlere, accessToken, webClient).call();
    }
}
