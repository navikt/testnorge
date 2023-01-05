package no.nav.dolly.bestilling.instdata.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class InstdataTestGetCommand implements Callable<Mono<ResponseEntity<JsonNode>>> {

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<ResponseEntity<JsonNode>> call() {

        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/institusjonsopphold/person")
                                .queryParam("environments", miljoe)
                                .build()
                )
                .header("norskident", ident)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toEntity(JsonNode.class);
    }
}
