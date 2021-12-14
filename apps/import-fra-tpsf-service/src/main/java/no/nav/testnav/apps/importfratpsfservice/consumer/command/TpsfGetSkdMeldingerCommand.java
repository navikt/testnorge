package no.nav.testnav.apps.importfratpsfservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class TpsfGetSkdMeldingerCommand implements Callable<Flux<SkdEndringsmelding>> {

    private static final String TPSF_SKD_GRUPPE_URL = "/api/v1/endringsmelding/skd/gruppe/meldinger/{gruppeId}/{pageNumber}";

    private final WebClient webClient;
    private final Long gruppeId;
    private final Long pageNumber;
    private final String token;

    @Override
    public Flux<SkdEndringsmelding> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(TPSF_SKD_GRUPPE_URL).build(gruppeId, pageNumber))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(SkdEndringsmelding.class);
    }
}
