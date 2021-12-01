package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;

@Slf4j
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
                .bodyToMono(ProfileDTO.class);
    }
}
