package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentetDTO;

@Slf4j
@DependencyOn("testnorge-arbeidsforhold-api")
@RequiredArgsConstructor
public class SaveOppsummeringsdokumenterCommand implements Callable<Void> {
    private final WebClient webClient;
    private final String accessToken;
    private final OppsummeringsdokumentetDTO opplysningspliktigDTO;
    private final String miljo;

    @Override
    public Void call() {
        log.info(
                "Send inn raport for opplysningspliktig {} den {}.",
                opplysningspliktigDTO.getOpplysningspliktigOrganisajonsnummer(),
                opplysningspliktigDTO.getKalendermaaned()
        );
        return webClient
                .put()
                .uri(builder -> builder.path("/api/v1/oppsummeringsdokumenter").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("miljo", miljo)
                .body(BodyInserters.fromPublisher(Mono.just(opplysningspliktigDTO), OppsummeringsdokumentetDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}