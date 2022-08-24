package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.exception.LagreSykemeldingException;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntsykemeldingapi.util.Headers.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class PostSykemeldingCommand implements Callable<Mono<ResponseEntity<String>>> {

    private final WebClient webClient;
    private final String token;
    private final SykemeldingDTO sykemelding;

    @SneakyThrows
    @Override
    public Mono<ResponseEntity<String>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/sykemelding/api/v1/sykemeldinger").build()
                )
                .header(AUTHORIZATION, "Bearer " + token)
                .bodyValue(sykemelding)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(throwable -> {
                    log.error("Feil oppsto i innsending av sykemelding", throwable);
                    throw new LagreSykemeldingException("Feil oppsto i innsending av sykemelding");
                });
    }
}
