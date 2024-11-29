package no.nav.registre.testnorge.sykemelding.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.sykemelding.dto.ReceivedSykemeldingDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.ValidationResultDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SyfosmreglerPostValidateCommand implements Callable<Mono<ValidationResultDTO>> {

    private static final String SYFOSMREGLER_VALIDATE_URL =  "/v1/rules/validate";

    private final WebClient webClient;
    private final ReceivedSykemeldingDTO receivedSykemelding;
    private final String accessToken;

    @Override
    public Mono<ValidationResultDTO> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(SYFOSMREGLER_VALIDATE_URL).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue(receivedSykemelding)
                .retrieve()
                .bodyToMono(ValidationResultDTO.class);
    }
}
