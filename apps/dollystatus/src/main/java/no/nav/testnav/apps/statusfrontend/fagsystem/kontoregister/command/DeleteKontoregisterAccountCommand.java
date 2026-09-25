package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.kontoregister.v1.SlettKontoRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class DeleteKontoregisterAccountCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/kontoregister/api/system/v1/slett-konto")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(new SlettKontoRequestDTO(ident, "Dolly"))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()
                            || response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody();
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<Void>error(exception));
                })
                .timeout(timeout);
    }
}
