package no.nav.registre.testnorge.helsepersonellservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.helsepersonellservice.exception.IdentNotFoundException;
import no.nav.testnav.libs.dto.samhandlerregisteret.v1.SamhandlerDTO;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetSamhandlerCommand implements Callable<SamhandlerDTO[]> {
    private final String ident;
    private final WebClient webClient;
    private final String token;

    @Override
    public SamhandlerDTO[] call() {
        try {
            log.info("Henter samhandlerinformasjon for ident {}", ident);
            SamhandlerDTO[] response = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/rest/sar/samh")
                            .queryParam("ident", ident)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(SamhandlerDTO[].class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

            if (response == null || response.length == 0) {
                throw new IdentNotFoundException("Finner ikke ident " + ident + " i samhandlerregisteret.");
            }
            return response;
        } catch (Exception e) {
            log.error("Feil ved henting av samhandlerinformasjon til ident {}", ident, e);
            throw e;
        }
    }
}
