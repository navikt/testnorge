package no.nav.registre.testnorge.helsepersonellservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;


@Slf4j
@RequiredArgsConstructor
public class GetAlleIdenterCommand implements Callable<Set<String>> {
    private final Long avspillergruppeId;
    private final WebClient webClient;
    private final String token;

    @Override
    public Set<String> call() {
        try {

            log.info("Henter alle identer fra avspillergruppe {}", avspillergruppeId);
            String[] identer = webClient.get().uri(builder -> builder.path("/api/v1/alle-identer/{avspillergruppeId}").build(avspillergruppeId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(String[].class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

            if (identer == null || identer.length == 0) {
                log.warn("Fant ingen identer for avspillergruppe {}", avspillergruppeId);
                return new HashSet<>();
            }
            log.info("Fant {} identer i avspillergruppe {}", identer.length, avspillergruppeId);
            return new HashSet<>(Arrays.asList(identer));
        } catch (Exception e) {
            log.error("Klarte ikke hente ut identer fra hodejegerern for avspillergruppe {}", avspillergruppeId, e);
            throw e;
        }
    }
}
