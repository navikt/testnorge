package no.nav.registre.orkestratoren.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.CREDENTIAL;
import static no.nav.freg.fasit.utils.domain.ResourceType.REST_SERVICE;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.Credentials;
import no.nav.freg.fasit.utils.domain.RestService;

@Component
@Profile("fasit")
@RequiredArgsConstructor
public class FasitClient {

    @Autowired
    private FasitService fasitService;

    @Value("${application.name}")
    private String applicationName;
    @Value("${fasit.test.environment}")
    private String environmentName;

    public Map<String, Object> resolveFasitProperties() {
        Map<String, Object> properties = new HashMap<>();

        Credentials credentials = fasitService.find("testnorges.ida.credential.tpsf", CREDENTIAL, environmentName, applicationName, FSS, Credentials.class);
        properties.put("testnorges.ida.credential.tpsf.username", credentials.getUsername());
        properties.put("testnorges.ida.credential.tpsf.password", fasitService.findSecret(credentials.getPasswordUrl()));
        RestService hodejegeren = fasitService.find("testnorge-hodejegeren.rest-api", REST_SERVICE, environmentName, applicationName, FSS, RestService.class);
        properties.put("testnorge-hodejegeren.rest-api.url", hodejegeren.getEndpointUrl());
        RestService inntektSynt = fasitService.find("synthdata-arena-inntekt.rest-api", REST_SERVICE, environmentName, applicationName, FSS, RestService.class);
        properties.put("synthdata-arena-inntekt.rest-api.url", inntektSynt.getEndpointUrl());

        return properties;
    }
}