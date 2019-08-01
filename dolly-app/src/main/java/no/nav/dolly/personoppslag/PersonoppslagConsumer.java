package no.nav.dolly.personoppslag;

import static no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag.GEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.sts.StsOidcService;

@Service
public class PersonoppslagConsumer {

    private static final String NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";
    private static final String NAV_CALL_ID = "Nav-Call-id";
    private static final String OPPLYSNINGSTYPER = "Opplysningstyper";
    private static final String TEMA = "Tema";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String PERSONOPPSLAG_URL = "/api/v1/oppslag?historikk=true";
    private static final String PREPROD_ENV = "q1";

    @Value("${fasit.environment.name}")
    private String environment;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    @Autowired
    private StsOidcService stsOidcService;

    public ResponseEntity fetchPerson(String ident) {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getPersonOppslag().getUrl() + PERSONOPPSLAG_URL))
                .header(AUTHORIZATION, resolveToken())
                .header(NAV_CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                .header(NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(PREPROD_ENV))
                .header(NAV_PERSON_IDENT, ident)
                .header(OPPLYSNINGSTYPER, "falskIdentitet,KontaktinformasjonForDoedsbo,UtenlandskIdentifikasjonsnummer")
                .header(TEMA, GEN.name())
                .build(), JsonNode.class);
    }

    private String resolveToken() {

        return PREPROD_ENV.equals(environment) ?  StsOidcService.getUserIdToken() : stsOidcService.getIdToken(PREPROD_ENV);
    }
}
