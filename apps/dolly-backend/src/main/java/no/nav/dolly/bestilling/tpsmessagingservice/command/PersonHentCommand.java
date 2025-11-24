package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class PersonHentCommand implements Callable<Flux<PersonMiljoeDTO>> {

    private static final String MILJOER_PARAM = "miljoer";
    private static final String PERSONER_URL = "/api/v2/personer/ident";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final String token;

    @Override
    public Flux<PersonMiljoeDTO> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSONER_URL)
                        .queryParam(MILJOER_PARAM, miljoer)
                        .build())
                .bodyValue(ident)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(PersonMiljoeDTO.class)
                .map(resultat -> {
                    resultat.setIdent(ident);
                    return resultat;
                })
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(PersonMiljoeDTO
                            .builder()
                            .status("FEIL")
                            .melding(description.getStatus().getReasonPhrase())
                            .utfyllendeMelding(description.getMessage())
                            .build());
                });
    }
}
