package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.IdentpoolLedigDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@RequiredArgsConstructor
@Slf4j
public class IdentpoolGetLedigCommand implements Callable<Flux<IdentpoolLedigDTO>> {

    private static final String PERSONIDENTIFIKATOR = "personidentifikator";
    private static final String IS_AVAIL_URL = "/api/v1/identifikator/ledig";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<IdentpoolLedigDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path(IS_AVAIL_URL).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .header(PERSONIDENTIFIKATOR, ident)
                .retrieve()
                .bodyToFlux(Boolean.class)
                .map(result -> IdentpoolLedigDTO.builder()
                        .ident(ident)
                        .ledig(isTrue(result))
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> Mono.just(IdentpoolLedigDTO.builder()
                        .ident(ident)
                        .ledig(false)
                        .build()));
    }
}
