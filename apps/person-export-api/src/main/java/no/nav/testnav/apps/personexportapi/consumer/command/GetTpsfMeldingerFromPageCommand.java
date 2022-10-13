package no.nav.testnav.apps.personexportapi.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personexportapi.consumer.dto.EndringsmeldingDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.personexportapi.consumer.command.GetKodeverkCommand.getMessage;

@Slf4j
@RequiredArgsConstructor
public class GetTpsfMeldingerFromPageCommand implements Callable<Mono<List<EndringsmeldingDTO>>> {

    private static final ParameterizedTypeReference<List<EndringsmeldingDTO>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final String token;
    private final String avspillingsgruppe;
    private final int pageNumber;

    @Override
    public Mono<List<EndringsmeldingDTO>> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/endringsmelding/skd/gruppe/meldinger/{avspillingsgruppe}/{pageNumber}")
                        .build(avspillingsgruppe, pageNumber)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .onErrorResume(throwable -> {
                    log.error("Feil i henting av tpsf mledinger: " + getMessage(throwable));
                    return Mono.just(Collections.emptyList());
                });
    }
}
