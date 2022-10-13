package no.nav.testnav.apps.personexportapi.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personexportapi.consumer.dto.GruppeDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.personexportapi.consumer.command.GetKodeverkCommand.getMessage;

@Slf4j
@RequiredArgsConstructor
public class GetTpsfGrupperCommand implements Callable<Mono<List<GruppeDTO>>> {
    private static final ParameterizedTypeReference<List<GruppeDTO>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<List<GruppeDTO>> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/endringsmelding/skd/grupper")
                        .build()
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .onErrorResume(throwable -> {
                    log.error("Feil i henting av tpsf grupper: " + getMessage(throwable));
                    return Mono.just(Collections.emptyList());
                });
    }
}
