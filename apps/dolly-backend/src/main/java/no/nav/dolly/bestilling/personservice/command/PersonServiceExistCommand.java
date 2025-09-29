package no.nav.dolly.bestilling.personservice.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PersonServiceExistCommand implements Callable<Mono<PersonServiceResponse>> {

    private final WebClient webClient;
    private final String ident;
    private final Set<String> opplysningId;
    private final String token;

    private static final String PERSON_URL = "/api/v1/personer/{ident}/exists";

    @Override
    public Mono<PersonServiceResponse> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(PERSON_URL)
                        .queryParamIfPresent("opplysningId", Optional.ofNullable(opplysningId.isEmpty() ? null : opplysningId))
                        .build(ident))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toEntity(Boolean.class)
                .map(resultat -> PersonServiceResponse.builder()
                        .ident(ident)
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .exists(resultat.getBody())
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> Mono.just(PersonServiceResponse.builder()
                        .exists(false)
                        .ident(ident)
                        .status(HttpStatus.OK)
                        .build()));
    }
}