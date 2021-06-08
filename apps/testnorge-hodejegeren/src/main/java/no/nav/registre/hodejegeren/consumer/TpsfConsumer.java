package no.nav.registre.hodejegeren.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import no.nav.registre.hodejegeren.consumer.command.GetTpsServiceRoutineCommand;
import no.nav.registre.hodejegeren.consumer.dto.ServiceRoutineDTO;


@Component
@Slf4j
public class TpsfConsumer {

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate urlGetIdenter;
    private UriTemplate urlServiceRoutine;
    private UriTemplate statusPaaIdenter;
    private final WebClient webClient;
    private final Executor executor;

    public TpsfConsumer(
            RestTemplateBuilder restTemplateBuilder,
            Executor executor,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.executor = executor;
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
        this.urlGetIdenter = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/identer/{avspillergruppeId}?aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}");
        this.urlServiceRoutine = new UriTemplate(serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={miljoe}&fnr={fnr}");
        this.statusPaaIdenter = new UriTemplate(serverUrl + "/v1/serviceroutine/FS03-FDLISTER-DISKNAVN-M?aksjonsKode={aksjonskode}&antallFnr={antallIdenter}&environment={miljoe}&nFnr={identer}");

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
        var getRequest = RequestEntity.get(urlGetIdenter.expand(avspillergruppeId, StringUtils.join(aarsakskode, ','), transaksjonstype)).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE_SET).getBody();
    }

    public JsonNode getTpsServiceRoutine(
            String routineName,
            String aksjonsKode,
            String miljoe,
            String fnr
    ) throws IOException {
        var getRequest = RequestEntity.get(urlServiceRoutine.expand(routineName, aksjonsKode, miljoe, fnr)).build();
        var response = restTemplate.exchange(getRequest, String.class);
        return new ObjectMapper().readTree(response.getBody());
    }

    public Mono<ServiceRoutineDTO> getTpsServiceRoutineV2(String routineName, String aksjonsKode, String miljoe, String fnr){
        return Mono.fromFuture(getFuture(routineName, aksjonsKode, miljoe, fnr));
    }


    private CompletableFuture<ServiceRoutineDTO> getFuture(String routineName, String aksjonsKode, String miljoe, String fnr) {
        return CompletableFuture.supplyAsync(
                () -> new GetTpsServiceRoutineCommand(webClient, routineName, aksjonsKode, miljoe, fnr).call(),
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
        var getRequest = RequestEntity.get(statusPaaIdenter.expand(aksjonskode, identer.size(), miljoe, identerSomString)).build();
        var response = restTemplate.exchange(getRequest, String.class);
        return new ObjectMapper().readTree(response.getBody()).findValue("response");
    }
}
