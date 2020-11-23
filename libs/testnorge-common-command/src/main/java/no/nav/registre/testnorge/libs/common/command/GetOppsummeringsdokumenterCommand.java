package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentetDTO;

@Slf4j
@DependencyOn("testnorge-arbeidsforhold-api")
@RequiredArgsConstructor
public class GetOppsummeringsdokumenterCommand implements Callable<List<OppsummeringsdokumentetDTO>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String miljo;

    @SneakyThrows
    @Override
    public List<OppsummeringsdokumentetDTO> call() {
        log.info("Henter alle oppsummeringsdokumenter.");
        try {
            OppsummeringsdokumentetDTO[] array = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/oppsummeringsdokumenter")
                            .build()
                    )
                    .header("miljo", this.miljo)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OppsummeringsdokumentetDTO[].class)
                    .block();
            return Arrays.stream(array).collect(Collectors.toList());
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}