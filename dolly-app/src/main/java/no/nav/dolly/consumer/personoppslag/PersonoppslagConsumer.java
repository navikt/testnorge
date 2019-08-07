package no.nav.dolly.consumer.personoppslag;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag.GEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PersonoppslagConsumer {

    private static final String OPPLYSNINGSTYPER = "Opplysningstyper";
    private static final String TEMA = "Tema";
    private static final String PERSONOPPSLAG_URL = "/api/v1/oppslag?historikk=true";
    private static final String PREPROD_ENV = "q1";

    @Value("${fasit.environment.name}")
    private String environment;

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    public ResponseEntity fetchPerson(String ident) {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getPersonOppslag().getUrl() + PERSONOPPSLAG_URL))
                .header(AUTHORIZATION, resolveToken())
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .header(OPPLYSNINGSTYPER, "KontaktinformasjonForDoedsbo,UtenlandskIdentifikasjonsnummer")
                .header(TEMA, GEN.name())
                .build(), JsonNode.class);
    }

    private String resolveToken() {

        return PREPROD_ENV.equals(environment) ? StsOidcService.getUserIdToken() : stsOidcService.getIdToken(PREPROD_ENV);
    }
}
