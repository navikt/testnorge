package no.nav.identpool.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.BASE_URL;
import static no.nav.freg.fasit.utils.domain.ResourceType.REST_SERVICE;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.BaseUrl;
import no.nav.freg.fasit.utils.domain.RestService;

@Component
@Profile("fasit")
@RequiredArgsConstructor
public class FasitClient {
    private final FasitService fasitService;

    @Value("${application.name}")
    private String applicationName;
    @Value("${test.environment}")
    private String environmentName;

    public Map<String, Object> resolveFasitProperties() throws IOException {
        Map<String, Object> properties = new HashMap<>();

        BaseUrl hostBaseUrl = fasitService.find("securityTokenService", BASE_URL, environmentName, applicationName, FSS, BaseUrl.class);
        properties.put("freg.security.oidc.idp.navsts.hostUrl", hostBaseUrl.getUrl());

        BaseUrl issuerBaseUrl = fasitService.find("security-token-service-issuer", BASE_URL, environmentName, applicationName, FSS, BaseUrl.class);
        properties.put("freg.security.oidc.idp.navsts.issuerUrl", issuerBaseUrl.getUrl());

        RestService restService = fasitService.find("security-token-service-jwks", REST_SERVICE, environmentName, applicationName, FSS, RestService.class);
        properties.put("freg.security.oidc.idp.navsts.jwksUrl", restService.getEndpointUrl());

//        Certificate certificate = fasitService.find("nav_truststore", CERTIFICATE, environmentName, applicationName, FSS, Certificate.class);
        //        System.setProperty("javax.net.ssl.trustStore", fasitService.downloadFromUrl(certificate.getKeystoreUrl(), "nav_truststore.jks"));
        //        System.setProperty("javax.net.ssl.trustStorePassword", fasitService.findSecret(certificate.getPasswordUrl()));

        return properties;
    }
}
