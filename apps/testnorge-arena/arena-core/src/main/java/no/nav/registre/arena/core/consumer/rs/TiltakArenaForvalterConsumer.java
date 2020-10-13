package no.nav.registre.arena.core.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.command.PostFinnTiltakCommand;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Component
@DependencyOn(value = "arena-forvalteren", external = true)
public class TiltakArenaForvalterConsumer {

    private final WebClient webClient;

    public TiltakArenaForvalterConsumer(
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaForvalterServerUrl).build();
    }

    public NyttVedtakResponse finnTiltak(RettighetFinnTiltakRequest rettighet) {
        return new PostFinnTiltakCommand(rettighet, webClient).call();
    }

}
