package no.nav.registre.testnorge.arena.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.command.PostEndreInnsatsbehovCommand;
import no.nav.registre.testnorge.arena.consumer.rs.request.EndreInnsatsbehovRequest;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@DependencyOn(value = "arena-forvalteren", external = true)
public class InnsatsArenaForvalterConsumer {

    private final WebClient webClient;

    public InnsatsArenaForvalterConsumer(
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaForvalterServerUrl).build();
    }

    public void endreInnsatsbehov(EndreInnsatsbehovRequest endreRequest) {
        var response = new PostEndreInnsatsbehovCommand(endreRequest, webClient).call();

        if (response.getNyeEndreInnsatsbehovFeilList() != null &&
                !response.getNyeEndreInnsatsbehovFeilList().isEmpty()) {
            log.info(String.format("Endring av innsatsbehov for ident %s feilet", endreRequest.getPersonident()));
        }
    }

}
