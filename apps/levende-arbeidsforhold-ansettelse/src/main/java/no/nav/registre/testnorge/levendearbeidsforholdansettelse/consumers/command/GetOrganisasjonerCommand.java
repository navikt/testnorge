package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.organisasjon.Organisasjon2;
//import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.organisasjon.dto.OrganisasjonDTO;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonerCommand implements Callable<List<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String token;


    @Override
    public List<OrganisasjonDTO> call()  {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/organisasjoner")
                        .queryParam("inkluderHierarki", true)
                        .queryParam("kanHaArbeidsforhold", true)
                        .build()
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .collectList()
                .block();
    }
}
