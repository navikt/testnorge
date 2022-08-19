package no.nav.testnav.apps.tpservice.consumer.rs;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpservice.consumer.rs.command.*;
import no.nav.testnav.apps.tpservice.consumer.rs.credential.PensjonforvalterProxyProperties;
import no.nav.testnav.apps.tpservice.consumer.rs.request.LagreTpForholdRequest;
import no.nav.testnav.apps.tpservice.consumer.rs.request.LagreTpYtelseRequest;
import no.nav.testnav.apps.tpservice.consumer.rs.response.PensjonforvalterResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class PensjonforvalterConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public PensjonforvalterConsumer(TokenExchange tokenExchange,
                                    PensjonforvalterProxyProperties serverProperties,
                                    ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public PensjonforvalterResponse lagreTpForhold(LagreTpForholdRequest lagreTpForholdRequest) {
        var response = tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new PostTpForholdCommand(webClient, accessToken.getTokenValue(), lagreTpForholdRequest).call())
                .block();

        if (isNull(response)) {
            throw new RuntimeException(String.format("Klarte ikke å lagre TP forhold for %s i PESYS (pensjon)", lagreTpForholdRequest.getFnr()));
        }
        return response;
    }

    public void sletteTpForhold(List<String> identer) {
        tokenExchange.exchange(serviceProperties)
                .flatMapMany(token -> new GetMiljoerCommand(webClient, token.getTokenValue()).call()
                        .flatMapMany(miljoer -> Flux.range(0, identer.size())
                                .map(index -> new SletteTpForholdCommand(webClient, identer.get(index), miljoer, token.getTokenValue()).call())))
                .flatMap((Flux::from))
                .collectList()
                .subscribe(response -> log.info("Slettet mot PESYS (tp) i alle miljoer"));
    }

    public JsonNode getTpForhold(String ident, String miljoe) {
        var response = tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetTpForholdCommand(webClient, accessToken.getTokenValue(), ident, miljoe).call())
                .block();
        if (isNull(response)) {
            throw new RuntimeException(String.format("Klarte ikke å hente TP forhold for %s i %s fra TP (pensjon)", ident, miljoe));
        }
        return response;
    }

    public PensjonforvalterResponse lagreTpYtelse(LagreTpYtelseRequest lagreTpYtelseRequest) {

        var response = tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new PostTpYtelseCommand(webClient, accessToken.getTokenValue(), lagreTpYtelseRequest).call())
                .block();
        if (isNull(response)) {
            throw new RuntimeException(String.format("Feilet å lagre TP-ytelse for %s i PESYS (pensjon)", lagreTpYtelseRequest.getFnr()));
        }
        if (isNull(response.getStatus()) || response.getStatus().isEmpty()) {
            throw new RuntimeException(String.format("Klarte ikke å få TP-ytelse respons for %s i PESYS (pensjon)", lagreTpYtelseRequest.getFnr()));
        }
        return response;

    }

}
