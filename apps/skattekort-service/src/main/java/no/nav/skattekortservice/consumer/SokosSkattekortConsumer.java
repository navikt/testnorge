package no.nav.skattekortservice.consumer;

import no.nav.skattekortservice.config.Consumers;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SokosSkattekortConsumer {

    private WebClient webClient;
    private TokenService tokenService;
    private ServerProperties serverProperties;

    public SokosSkattekortConsumer(TokenService tokenService, Consumers consumers) {

        this.serverProperties = consumers.getSokosSkattekort();
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenService = tokenService;
    }


}
