package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetProfileImageCommand implements Callable<byte[]> {

    private final WebClient webClient;
    private final String accessToken;

    @Override
    public byte[] call() {

        try {
            return webClient
                    .get()
                    .uri(builder -> builder.path("/v1.0/me/photos/240x240/$value").build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(
                            HttpStatus::isError,
                            clientResponse -> clientResponse
                                    .bodyToMono(String.class)
                                    .map(IllegalStateException::new)
                    )
                    .bodyToMono(byte[].class)
                    .block();
        } catch (IllegalStateException e) {
            log.warn("Bilde for bruker ikke funnet, melding: ", e);
            return new byte[0];
        }
    }
}
