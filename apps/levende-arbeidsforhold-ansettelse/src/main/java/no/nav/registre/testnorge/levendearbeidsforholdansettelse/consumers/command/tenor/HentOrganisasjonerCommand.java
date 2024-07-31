package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.tenor;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentOrganisasjonerCommand implements Callable<List<OrganisasjonDTO>> {
    private static final String PATH = "/api/v1/tenor/testdata/organisasjoner/oversikt";
    private final WebClient webClient;
    private final String token;
    private final TenorOrganisasjonRequest tenorOrgRequest;
    private final String antallOrganisasjoner;

    @SneakyThrows
    @Override
    public List<OrganisasjonDTO> call()  {
        return webClient
                .post()
                .uri(builder -> builder
                        .path(PATH)
                        .queryParam("antall", antallOrganisasjoner)
                        .build()
                )
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(tenorOrgRequest))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .collectList()
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
