package no.nav.registre.skd.commands.tpsf;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SlettIdenterFraTpsCommand implements Runnable {
    private static final int PAGE_SIZE = 100;
    private final WebClient webClient;
    private final List<String> miljoer;
    private final List<String> identer;

    @Override
    public void run() {
        log.info("Sletter {} ident(er) fra miljø: [{}]", identer.size(), String.join(", ", miljoer));
        var partisjonerteIdenter = Lists.partition(identer, PAGE_SIZE);
        try {
            partisjonerteIdenter.forEach(partisjon -> webClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/endringsmelding/skd/deleteFromTps")
                            .queryParam("miljoer", String.join(",", miljoer))
                            .queryParam("identer", String.join(",", partisjon))
                            .build())
                    .exchange()
                    .subscribe(clientResponse ->
                            log.info("Slettet identer {}.", clientResponse.statusCode())));
                    /*.bodyToMono(Void.class)
                    .block());*/
        } catch (HttpClientErrorException e) {
            log.error("Kunne ikke slette ident fra TPS. ", e);
            throw e;
        }
    }
}
