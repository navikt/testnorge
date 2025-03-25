package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.config.Consumers;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pensjon.PostPensjonTestdataInntektCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pensjon.PostPensjonTestdataPersonCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataInntekt;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Slf4j
@Component
public class PensjonTestdataFacadeConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public PensjonTestdataFacadeConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavPensjonTestdataFacadeProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public PensjonTestdataResponse opprettPerson(
            PensjonTestdataPerson person
    ) {
        try {
            return tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new PostPensjonTestdataPersonCommand(webClient, person, accessToken.getTokenValue()).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å opprette pensjon testdata person.", e);
            return PensjonTestdataResponse.builder().status(Collections.emptyList()).build();
        }
    }

    public PensjonTestdataResponse opprettInntekt(
            PensjonTestdataInntekt inntekt
    ) {
        try {
            return tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new PostPensjonTestdataInntektCommand(webClient, inntekt, accessToken.getTokenValue()).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å opprette pensjon testdata inntekt.", e);
            return PensjonTestdataResponse.builder().status(Collections.emptyList()).build();
        }

    }
}
