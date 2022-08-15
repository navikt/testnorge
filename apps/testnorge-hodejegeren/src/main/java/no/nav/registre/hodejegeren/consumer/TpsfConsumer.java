package no.nav.registre.hodejegeren.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.consumer.command.GetTpsIdenterCommand;
import no.nav.registre.hodejegeren.consumer.command.GetTpsServiceRoutineV1Command;
import no.nav.registre.hodejegeren.consumer.command.GetTpsStatusPaaIdenterCommand;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import no.nav.registre.hodejegeren.consumer.command.GetTpsServiceRoutineV2Command;
import no.nav.registre.hodejegeren.consumer.dto.ServiceRoutineDTO;


@Component
@Slf4j
public class TpsfConsumer {
    private final WebClient webClient;
    private final Executor executor;

    public TpsfConsumer(
            Executor executor,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.executor = executor;

        HttpClient client = HttpClient
                .create()
                .responseTimeout(Duration.ofMinutes(2));

        this.webClient = WebClient
                .builder()
                .baseUrl(serverUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = {"operation", "tpsf"})
    public Set<String> getIdenterFiltrertPaaAarsakskode(
            Long avspillergruppeId,
            List<String> aarsakskode,
            String transaksjonstype
    ) {
        return new GetTpsIdenterCommand(webClient, StringUtils.join(aarsakskode, ','), transaksjonstype, avspillergruppeId).call();
    }

    public JsonNode getTpsServiceRoutine(
            String routineName,
            String aksjonsKode,
            String miljoe,
            String fnr
    ) throws IOException {
        var response = new GetTpsServiceRoutineV1Command(webClient, routineName, aksjonsKode, miljoe, fnr).call();
        return new ObjectMapper().readTree(response);
    }

    public Mono<ServiceRoutineDTO> getTpsServiceRoutineV2(String routineName, String aksjonsKode, String miljoe, String fnr){
        return Mono.fromFuture(getFuture(routineName, aksjonsKode, miljoe, fnr));
    }


    private CompletableFuture<ServiceRoutineDTO> getFuture(String routineName, String aksjonsKode, String miljoe, String fnr) {
        return CompletableFuture.supplyAsync(
                () -> new GetTpsServiceRoutineV2Command(webClient, routineName, aksjonsKode, miljoe, fnr).call(),
                executor
        );
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = {"operation", "tpsf"})
    public JsonNode hentTpsStatusPaaIdenter(
            String aksjonskode,
            String miljoe,
            List<String> identer
    ) throws IOException {
        var identerSomString = String.join(",", identer);
        var response = new GetTpsStatusPaaIdenterCommand(webClient, aksjonskode, identer.size(), miljoe, identerSomString).call();
        return new ObjectMapper().readTree(response).findValue("response");
    }
}
