package no.nav.testnav.apps.hodejegeren.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import no.nav.testnav.apps.hodejegeren.config.Consumers;
import no.nav.testnav.apps.hodejegeren.consumer.command.GetTpsIdenterCommand;
import no.nav.testnav.apps.hodejegeren.consumer.command.GetTpsServiceRoutineV1Command;
import no.nav.testnav.apps.hodejegeren.consumer.command.GetTpsServiceRoutineV2Command;
import no.nav.testnav.apps.hodejegeren.consumer.command.GetTpsStatusPaaIdenterCommand;
import no.nav.testnav.apps.hodejegeren.consumer.dto.ServiceRoutineDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
public class TpsfConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public TpsfConsumer(
            Consumers consumers,
            TokenExchange tokenExchange
    ) {
        serverProperties = consumers.getTpsfProxy();
        this.tokenExchange = tokenExchange;
        HttpClient client = HttpClient
                .create()
                .responseTimeout(Duration.ofMinutes(2));
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(
                        ExchangeStrategies
                                .builder()
                                .codecs(
                                        configurer -> configurer
                                                .defaultCodecs()
                                                .maxInMemorySize(16 * 1024 * 1024))
                                .build())
                .baseUrl(serverProperties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = {"operation", "tpsf"})
    public Set<String> getIdenterFiltrertPaaAarsakskode(
            Long avspillergruppeId,
            List<String> aarsakskode,
            String transaksjonstype
    ) {
        return tokenExchange.exchange(serverProperties).flatMap(accessToken -> new GetTpsIdenterCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        StringUtils.join(aarsakskode, ','),
                        transaksjonstype, avspillergruppeId).call())
                .block();
    }

    public JsonNode getTpsServiceRoutine(
            String routineName,
            String aksjonsKode,
            String miljoe,
            String fnr
    ) throws IOException {
        var response = tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetTpsServiceRoutineV1Command(
                        webClient, accessToken.getTokenValue(), routineName, aksjonsKode, miljoe, fnr).call()).block();
        return new ObjectMapper().readTree(response);
    }

    public Mono<AccessToken> getToken() {
        return tokenExchange.exchange(serverProperties);
    }

    public Flux<ServiceRoutineDTO> getTpsServiceRoutineV2(String routineName, String aksjonsKode, String miljoe, String fnr, AccessToken token) {
        return new GetTpsServiceRoutineV2Command(webClient, token.getTokenValue(), routineName, aksjonsKode, miljoe, fnr).call();
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = {"operation", "tpsf"})
    public JsonNode hentTpsStatusPaaIdenter(
            String aksjonskode,
            String miljoe,
            List<String> identer
    ) throws IOException {
        var identerSomString = String.join(",", identer);
        var response = tokenExchange.exchange(serverProperties).flatMap(accessToken ->
                        new GetTpsStatusPaaIdenterCommand(
                                webClient, accessToken.getTokenValue(), aksjonskode, identer.size(), miljoe, identerSomString).call())
                .block();
        return new ObjectMapper().readTree(response).findValue("response");
    }
}
