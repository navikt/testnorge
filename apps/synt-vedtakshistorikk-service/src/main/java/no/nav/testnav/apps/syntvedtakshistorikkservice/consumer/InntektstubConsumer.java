package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.inntektstub.PostInntekterCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.InntektstubProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.inntektstub.Inntektsinformasjon;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Slf4j
@Service
public class InntektstubConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public InntektstubConsumer(
            InntektstubProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.tokenExchange = tokenExchange;
    }

    public List<Inntektsinformasjon> postInntekter(List<Inntektsinformasjon> inntektsinformasjon) {
        try {
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new PostInntekterCommand(inntektsinformasjon, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke opprette inntektstub inntekt", e);
            return null;
        }
    }
}
