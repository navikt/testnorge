package no.nav.registre.testnorge.synt.tiltakservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Slf4j
@Component
@DependencyOn(value = "arena-forvalteren", external = true)
public class ArenaForvalterConsumer {

    private final WebClient webClient;

    public ArenaForvalterConsumer(
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaForvalterServerUrl).build();
    }
}
