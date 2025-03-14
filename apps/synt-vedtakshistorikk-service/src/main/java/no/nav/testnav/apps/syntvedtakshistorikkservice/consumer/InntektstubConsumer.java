package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.config.Consumers;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.inntektstub.DeleteInntekterCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.inntektstub.PostInntekterCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.inntektstub.Inntektsinformasjon;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;


@Slf4j
@Service
public class InntektstubConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public InntektstubConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavInntektstubProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public List<Inntektsinformasjon> postInntekter(List<Inntektsinformasjon> inntektsinformasjon) {
        try {
            log.info("Oppretter inntekt i inntektstub for ident");
            return tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new PostInntekterCommand(inntektsinformasjon, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke opprette inntektstub inntekt", e);
            return Collections.emptyList();
        }
    }

    public void deleteInntekter(List<String> identer) {

            log.info("Sletter ident(er) fra Inntektstub");
            tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new DeleteInntekterCommand(identer, accessToken.getTokenValue(), webClient).call())
                    .subscribe(response -> log.info("Slettet identer fra Inntektstub"));
    }
}
