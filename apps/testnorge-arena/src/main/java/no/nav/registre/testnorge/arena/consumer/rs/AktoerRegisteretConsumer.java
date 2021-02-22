package no.nav.registre.testnorge.arena.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.command.aktoer.GetAktoerIdTilIdenterCommand;
import no.nav.registre.testnorge.arena.security.TokenService;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@DependencyOn(value = "aktoerregister", external = true)
public class AktoerRegisteretConsumer {

    private final WebClient webClient;

    private final TokenService tokenService;

    private final String aktoerUrl;

    public AktoerRegisteretConsumer(
            TokenService tokenService,
            @Value("${aktoerregister.api.url}") String aktoerUrl
    ) {
        this.tokenService = tokenService;
        this.aktoerUrl = aktoerUrl;
        this.webClient = WebClient.builder().build();
    }

    public Map<String, String> hentAktoerIderTilIdenter(
            List<String> identer,
            String miljoe
    ) {
        var baseUrl = String.format(aktoerUrl, miljoe);

        var response = new GetAktoerIdTilIdenterCommand(identer, baseUrl, tokenService.getIdToken(), webClient).call();

        Map<String, String> aktoerFnr = new HashMap<>();
        if (response != null && !response.isEmpty()) {
            for (var entry : response.entrySet()) {
                var aktoerId = "";
                if (entry.getValue().getIdenter() == null) {
                    continue;
                }
                for (var aktoer : entry.getValue().getIdenter()) {
                    if (aktoer.isGjeldende() && "AktoerId".equals(aktoer.getIdentgruppe())) {
                        aktoerId = aktoer.getIdent();
                        break;
                    }
                }
                aktoerFnr.put(entry.getKey(), aktoerId);
            }
        }
        return aktoerFnr;
    }
}
