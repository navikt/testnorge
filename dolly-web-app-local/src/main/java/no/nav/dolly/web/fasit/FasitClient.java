package no.nav.dolly.web.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.OPEN_ID_CONNECT;
import static no.nav.freg.fasit.utils.domain.ResourceType.REST_SERVICE;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.OpenIdConnect;
import no.nav.freg.fasit.utils.domain.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("fasit")
public class FasitClient {

    @Autowired
    private FasitService fasitService;

    @Value("${application.name}")
    private String applicationName;

    @Value("${test.environment}")
    private String environmentName;

    public Map<String, Object> resolveFasitProperties() {
        Map<String, Object> properties = new HashMap<>();

        OpenIdConnect oidc = fasitService.find("dolly-web-oidc", OPEN_ID_CONNECT, environmentName, applicationName, FSS, OpenIdConnect.class);

        properties.put("idp.openAm.clientId", oidc.getAgentName());
        properties.put("idp.openAm.clientSecret", fasitService.findSecret(oidc.getPasswordUrl()));
        properties.put("idp.openAm.hostUrl", oidc.getHostUrl());
        properties.put("idp.openAm.jwksUrl", oidc.getJwksUrl());
        properties.put("idp.openAm.issuerUrl", oidc.getIssuerUrl());

        return properties;
    }
}
