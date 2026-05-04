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

        log.info("PersonServiceExistCommand: Starter kall for ident {} med opplysningId {}", ident, opplysningId);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(PERSON_URL)
                        .queryParamIfPresent("opplysningId", Optional.ofNullable(opplysningId.isEmpty() ? null : opplysningId))
                        .build(ident))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toEntity(Boolean.class)
                .doOnSuccess(response -> log.info("PersonServiceExistCommand: Mottok respons for ident {}: status={}, body={}", 
                        ident, response.getStatusCode(), response.getBody()))
                .map(resultat -> PersonServiceResponse.builder()
                        .ident(ident)
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .exists(resultat.getBody())
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .doOnError(throwable -> log.error("PersonServiceExistCommand: Feil for ident {}: exceptionType={}, message={}", 
                        ident, throwable.getClass().getName(), throwable.getMessage(), throwable))
                .onErrorResume(throwable -> {
                    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                    String feilmelding = "%s: %s".formatted(throwable.getClass().getSimpleName(), throwable.getMessage());
                    
                    if (throwable instanceof WebClientResponseException wcre) {
                        status = HttpStatus.valueOf(wcre.getStatusCode().value());
                        feilmelding = wcre.getResponseBodyAsString();
                    }
                    
                    log.error("PersonServiceExistCommand: Returnerer feilrespons for ident {}: status={}, feilmelding={}", 
                            ident, status, feilmelding);
                    
                    return Mono.just(PersonServiceResponse.builder()
                            .exists(false)
                            .ident(ident)
                            .status(status)
                            .feilmelding(feilmelding)
                            .build());
                });
    }
}