package no.nav.registre.testnorge.arena.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.command.tpsf.GetPersonstatusOgAdresseCommand;
import no.nav.registre.testnorge.arena.consumer.rs.response.tpsf.Personstatus;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Slf4j
@Component
@DependencyOn(value = "tps-forvalteren", external = true)
public class TpsForvalterConsumer {

    private final WebClient webClient;

    public TpsForvalterConsumer(@Value("${tps-forvalteren.rest-api.url}") String tpsForvalterUrl) {
        this.webClient = WebClient.builder().baseUrl(tpsForvalterUrl).build();
    }

    public Personstatus getPersonstatus(String ident, String miljoe) {
        var personstatusOgAdresse = new GetPersonstatusOgAdresseCommand(ident, miljoe, webClient).call();
        if (personstatusOgAdresse != null && personstatusOgAdresse.getPersonstatus() != null) {
            return personstatusOgAdresse.getPersonstatus();
        } else {
            return new Personstatus();
        }
    }
}
