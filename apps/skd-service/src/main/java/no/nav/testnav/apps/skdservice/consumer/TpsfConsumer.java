package no.nav.testnav.apps.skdservice.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.skdservice.consumer.command.GetMeldingsIdsCommand;
import no.nav.testnav.apps.skdservice.consumer.command.PostSendSkdMeldingerTpsCommand;
import no.nav.testnav.apps.skdservice.consumer.command.requests.SendToTpsRequest;
import no.nav.testnav.apps.skdservice.consumer.response.SkdMeldingerTilTpsRespons;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@Slf4j
public class TpsfConsumer {
    private final WebClient webClient;

    public TpsfConsumer(
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.webClient = WebClient.builder().baseUrl(serverUrl)
                .defaultHeaders(header -> header.setBasicAuth(username, password))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public SkdMeldingerTilTpsRespons sendSkdmeldingerToTps(
            Long gruppeId,
            SendToTpsRequest sendToTpsRequest
    ) {
        log.info("Sender skd-meldinger med avspillergruppe {} til tps", gruppeId);
        return new PostSendSkdMeldingerTpsCommand(gruppeId, sendToTpsRequest, webClient).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIdsFromAvspillergruppe(Long gruppeId) {
        return new GetMeldingsIdsCommand(gruppeId, webClient).call();
    }
}
