package no.nav.registre.testnorge.organisasjonfastedataservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOrdreCommand implements Callable<List<ItemDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String ordreId;

    @Override
    public List<ItemDTO> call() {
        log.info("Henter ordre med ordreId {}.", ordreId);
        try {
            var response = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/order/{ordreId}/items")
                            .build(ordreId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(ItemDTO[].class)
                    .retryWhen(WebClientError.is5xxException())
                    .block();
            return Optional
                    .ofNullable(response)
                    .map(Arrays::asList)
                    .orElse(List.of());
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Fant ikke ordre med ordreId {}.", ordreId);
            return null;
        }
    }
}
