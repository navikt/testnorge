package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.Consumers;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.PostSykemeldingCommand;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SykemeldingConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public SykemeldingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {
        serverProperties = consumers.getSykemeldingApi();
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public void opprettSykemelding(SykemeldingDTO sykemelding) {
        tokenExchange.exchange(serverProperties).flatMap(accessToken ->
                        new PostSykemeldingCommand(webClient, accessToken.getTokenValue(), sykemelding).call())
                .block();
    }
}
