package no.nav.registre.testnorge.helsepersonell.consumer.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.dto.samhandlerregisteret.v1.SamhandlerDTO;

@Slf4j
public class GetSamhandlerCommand implements Callable<SamhandlerDTO[]> {
    private final String ident;
    private final UriTemplate uriTemplate;
    private final RestTemplate restTemplate;

    public GetSamhandlerCommand(String url, String ident, RestTemplate restTemplate) {
        this.uriTemplate = new UriTemplate(url + "?ident={ident}");
        this.restTemplate = restTemplate;
        this.ident = ident;
    }

    @Override
    public SamhandlerDTO[] call() {
        try {
            log.info("Henter samhandlerinformasjon for ident {}", ident);

            SamhandlerDTO[] samhandlereDTOs = restTemplate.getForObject(uriTemplate.expand(ident), SamhandlerDTO[].class);
            if (samhandlereDTOs == null || samhandlereDTOs.length == 0) {
                throw new RuntimeException("Finer ikke ident " + ident + " i samhandlerregisteret.");
            }
            return samhandlereDTOs;
        } catch (Exception e) {
            log.error("Feil ved henting av samhandlerinformasjon til ident {}", ident, e);
            throw e;
        }
    }
}
