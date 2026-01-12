package no.nav.dolly.bestilling.personservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PersonServiceExistCommand implements Callable<Mono<PersonServiceResponse>> {

    private static final String PERSON_URL = "/api/v1/personer/{ident}/exists";
    private final WebClient webClient;
    private final String ident;
    private final Set<String> opplysningId;
    private final String token;

    @Override
    public Mono<PersonServiceResponse> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(PERSON_URL)
                        .queryParamIfPresent("opplysningId", Optional.ofNullable(opplysningId.isEmpty() ? null : opplysningId))
                        .build(ident))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toEntity(Boolean.class)
                .doOnSuccess(response -> log.debug("PersonServiceExistCommand: Mottok respons for ident {}: status={}, body={}", 
                        ident, response.getStatusCode(), response.getBody()))
                .map(resultat -> PersonServiceResponse.builder()
                        .ident(ident)
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .exists(resultat.getBody())
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .doOnError(throwable -> {
                    if (throwable instanceof WebClientResponseException wcre) {
                        log.error("PersonServiceExistCommand: Feil for ident {}: status={}, body={}, error={}", 
                                ident, wcre.getStatusCode(), wcre.getResponseBodyAsString(), wcre.getMessage(), wcre);
                    } else {
                        log.error("PersonServiceExistCommand: Feil for ident {}: {} - {}", 
                                ident, throwable.getClass().getSimpleName(), throwable.getMessage(), throwable);
                    }
                })
                .onErrorResume(throwable -> {
                    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                    String feilmelding = throwable.getMessage();
                    
                    if (throwable instanceof WebClientResponseException wcre) {
                        status = HttpStatus.valueOf(wcre.getStatusCode().value());
                        feilmelding = wcre.getResponseBodyAsString();
                    }
                    
                    return Mono.just(PersonServiceResponse.builder()
                            .exists(false)
                            .ident(ident)
                            .status(status)
                            .feilmelding(feilmelding)
                            .build());
                });
    }
}