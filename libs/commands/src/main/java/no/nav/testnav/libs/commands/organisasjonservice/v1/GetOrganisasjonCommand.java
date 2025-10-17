package no.nav.testnav.libs.commands.organisasjonservice.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<OrganisasjonDTO> {

    private final WebClient webClient;
    private final String token;
    private final String orgnummer;
    private final String miljo;

    @Override
    public OrganisasjonDTO call() {
        log.info("Henter organisasjon med orgnummer {} fra {}...", orgnummer, miljo);
        try {
            var organiasjon = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/organisasjoner/{orgnummer}")
                            .build(orgnummer))
                    .headers(WebClientHeader.bearer(token))
                    .header("miljo", this.miljo)
                    .retrieve()
                    .bodyToMono(OrganisasjonDTO.class)
                    .retryWhen(WebClientError.is5xxException())
                    .block();
            log.info("Organisasjon {} hentet fra {}", orgnummer, miljo);
            return organiasjon;
        } catch (WebClientResponseException.NotFound e) {
            log.trace("Organisasjon med orgnummer {} ikke funnet i {}", orgnummer, miljo);
            return null;
        }
    }
}
