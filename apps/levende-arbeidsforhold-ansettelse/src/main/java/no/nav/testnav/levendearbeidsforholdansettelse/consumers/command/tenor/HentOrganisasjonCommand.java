package no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.tenor;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class HentOrganisasjonCommand implements Callable<Flux<OrganisasjonDTO>> {

    private final WebClient webClient;
    private final String token;
    private final TenorOrganisasjonRequest tenorOrgRequest;

    @Override
    public Flux<OrganisasjonDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder
                        .path("/api/v1/tenor/testdata/organisasjoner")
                        .build())
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(tenorOrgRequest))
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
