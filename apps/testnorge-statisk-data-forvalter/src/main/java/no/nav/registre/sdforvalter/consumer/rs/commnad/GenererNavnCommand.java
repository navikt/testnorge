package no.nav.registre.sdforvalter.consumer.rs.commnad;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;

@RequiredArgsConstructor
public class GenererNavnCommand implements Callable<NavnDTO> {
    private final WebClient webClient;
    private final String token;

    @Override
    public NavnDTO call() {
        return webClient
                .get()
                .uri("/api/v1/navn")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(NavnDTO.class)
                .block();
    }
}