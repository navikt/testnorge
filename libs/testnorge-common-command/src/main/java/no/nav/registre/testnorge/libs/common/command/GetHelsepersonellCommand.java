package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;

@RequiredArgsConstructor
public class GetHelsepersonellCommand implements Callable<HelsepersonellListeDTO> {
    private final WebClient webClient;
    private final String accessToken;

    @SneakyThrows
    @Override
    public HelsepersonellListeDTO call() {
        return webClient
                .get()
                .uri("/api/v1/helsepersonell")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(HelsepersonellListeDTO.class)
                .block();
    }
}