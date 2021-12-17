package no.nav.testnav.apps.importfratpsfservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class DollyPutIdentCommand implements Callable<Mono<Void>> {

    private static final String DOLLY_GRUPPE_URL = "/api/v1/gruppe/{gruppeId}/ident/{ident}";
    private static final String MASTER = "master";

    private final WebClient webClient;
    private final Long gruppeId;
    private final String ident;
    private final Master master;
    private final String token;

    @Override
    public Mono<Void> call() {

        return webClient
                .put()
                .uri(builder -> builder.path(DOLLY_GRUPPE_URL)
                        .queryParam(MASTER, master)
                        .build(gruppeId, ident))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
