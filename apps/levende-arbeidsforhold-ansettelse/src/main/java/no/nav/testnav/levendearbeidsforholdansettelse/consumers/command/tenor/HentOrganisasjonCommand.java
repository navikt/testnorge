package no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.tenor;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentOrganisasjonCommand implements Callable<Flux<OrganisasjonDTO>> {
    private static final String PATH = "/api/v1/tenor/testdata/organisasjoner";

    private final WebClient webClient;
    private final String token;
    private final TenorOrganisasjonRequest tenorOrgRequest;

    @SneakyThrows
    @Override
    public Flux<OrganisasjonDTO> call() {

        return webClient
                .post()
                .uri(builder -> builder
                        .path(PATH)
                        .build()
                )
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(tenorOrgRequest))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
