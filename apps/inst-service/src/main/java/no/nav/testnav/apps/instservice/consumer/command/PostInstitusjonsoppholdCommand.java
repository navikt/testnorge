package no.nav.testnav.apps.instservice.consumer.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.instservice.domain.Institusjonsopphold;
import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.provider.responses.OppholdResponse;
import no.nav.testnav.apps.instservice.util.WebClientFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.ACCEPT;
import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class PostInstitusjonsoppholdCommand implements Callable<Mono<OppholdResponse>> {
    private final WebClient webClient;
    private final String token;
    private final String miljoe;
    private final InstitusjonsoppholdV2 institusjonsopphold;

    protected static String getMessage(Throwable error) {
        return error instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString() :
                error.getMessage();
    }

    @SneakyThrows
    @Override
    public Mono<OppholdResponse> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/institusjonsopphold/person")
                                .queryParam("environments", miljoe)
                                .build()
                )
                .header(ACCEPT, "application/json")
                .header(AUTHORIZATION, "Bearer " + token)
                .bodyValue(institusjonsopphold)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .then(Mono.just(OppholdResponse.builder()
                        .status(HttpStatus.OK)
                        .institusjonsopphold(Institusjonsopphold.builder()
                                .personident(institusjonsopphold.getNorskident())
                                .tssEksternId(institusjonsopphold.getTssEksternId())
                                .startdato(institusjonsopphold.getStartdato())
                                .forventetSluttdato(institusjonsopphold.getSluttdato())
                                .registrertAv(institusjonsopphold.getRegistrertAv())
                                .institusjonstype(institusjonsopphold.getInstitusjonstype())
                                .build())
                        .build()))
                .onErrorResume(throwable -> {
                    var message = getMessage(throwable);
                    log.error("Feil oppsto under opprettelse av institusjonsopphold: " + message);

                    var status = HttpStatus.INTERNAL_SERVER_ERROR;
                    if (throwable instanceof WebClientResponseException) {
                        status = ((WebClientResponseException) throwable).getStatusCode();
                    }
                    return Mono.just(OppholdResponse.builder()
                            .status(status)
                            .feilmelding(message)
                            .build());
                });
    }
}
