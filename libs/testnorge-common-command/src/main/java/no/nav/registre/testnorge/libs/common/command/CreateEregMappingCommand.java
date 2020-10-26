package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.eregmapper.v1.EregMapperDTO;

@RequiredArgsConstructor
@DependencyOn("testnorge-ereg-mapper")
public class CreateEregMappingCommand implements Runnable {
    private final WebClient webClient;
    private final EregMapperDTO dto;
    private final String miljoe;

    @Override
    public void run() {
        webClient
                .post()
                .uri(builder -> builder
                        .path("/api/v1/orkestrering/opprett")
                        .queryParam("lastOpp", true)
                        .queryParam("miljoe", miljoe)
                        .build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(new EregMapperDTO[]{dto}), EregMapperDTO[].class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
