package no.nav.registre.sigrun.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sigrun.consumer.rs.command.PostSyntPoppMeldingerCommand;
import no.nav.registre.sigrun.consumer.rs.credential.SyntPoppProperties;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.sigrun.domain.PoppSyntetisererenResponse;

@Component
@Slf4j
public class PoppSyntetisererenConsumer {

    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public PoppSyntetisererenConsumer(
            SyntPoppProperties syntProperties,
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


    public List<PoppSyntetisererenResponse> hentPoppMeldingerFromSyntRest(List<String> fnrs) {
        var token = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();
        return new PostSyntPoppMeldingerCommand(fnrs, token, webClient).call();
    }
}
