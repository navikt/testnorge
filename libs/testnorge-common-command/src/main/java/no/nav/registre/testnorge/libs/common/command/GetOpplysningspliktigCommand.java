package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;

@Slf4j
@DependencyOn("arbeidsforhold-api")
@RequiredArgsConstructor
public class GetOpplysningspliktigCommand implements Callable<OpplysningspliktigDTO> {
    private final WebClient webClient;
    private final String accessToken;
    private final String orgnummer;
    private final LocalDate kalendermaaned;
    private final String miljo;

    @SneakyThrows
    @Override
    public OpplysningspliktigDTO call() {
        log.info("Henter opplysningspliktig med orgnummer {}.", orgnummer);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/opplysningspliktig/{orgnummer}/{kalendermaaned}")
                            .build(orgnummer, kalendermaaned)
                    )
                    .header("miljo", this.miljo)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OpplysningspliktigDTO.class)
                    .block();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}