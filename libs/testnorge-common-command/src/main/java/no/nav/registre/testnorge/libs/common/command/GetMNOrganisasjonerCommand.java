package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

@Slf4j
@DependencyOn("mn-organisasjon-api")
@RequiredArgsConstructor
public class GetMNOrganisasjonerCommand implements Callable<List<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String accessToken;

    @SneakyThrows
    @Override
    public List<OrganisasjonDTO> call() {
        log.info("Henter alle Mini-Norge organiasjoner");
        try {
            OrganisasjonDTO[] array = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/organisasjoner/")
                            .build()
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OrganisasjonDTO[].class)
                    .block();
            return Arrays.asList(array);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}