package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.hodejegeren.v1.PersondataDTO;

@Slf4j
public class GetPersondataCommand implements Callable<PersondataDTO> {

    private final String miljoe;
    private final String ident;
    private final UriTemplate uriTemplate;
    private final RestTemplate restTemplate;

    public GetPersondataCommand(String url, String ident, String miljoe, RestTemplate restTemplate) {
        this.uriTemplate = new UriTemplate(url + "/api/v1/persondata?ident={ident}&miljoe={miljoe}");
        this.miljoe = miljoe;
        this.ident = ident;
        this.restTemplate = restTemplate;
    }

    @Override
    public PersondataDTO call() {
        try {
            log.info("Henter persondata fra hodejegerern for ident {} i miljø {}", ident, miljoe);
            return restTemplate.getForObject(uriTemplate.expand(ident, miljoe), PersondataDTO.class);
        } catch (Exception e){
            log.error("Klarte ikke hente ut persondata fra hodejegerern for ident {} i miljø {}", ident, miljoe, e);
            throw e;
        }
    }
}
