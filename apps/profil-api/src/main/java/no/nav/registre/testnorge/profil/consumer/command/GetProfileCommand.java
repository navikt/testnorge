package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetProfileCommand implements Callable<Mono<ProfileDTO>> {

    private final WebClient webClient;
    private final String accessToken;

    @Override
    public Mono<ProfileDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/v1.0/me/").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(ProfileDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(WebClientError.is5xxException());
    }

}
