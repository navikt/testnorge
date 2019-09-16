package no.nav.dolly.local.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.CREDENTIAL;
import static no.nav.freg.fasit.utils.domain.ResourceType.DATASOURCE;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import lombok.RequiredArgsConstructor;
import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.Credentials;
import no.nav.freg.fasit.utils.domain.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Profile("fasit")
public class FasitClient {

    private final FasitService fasitService;

    @Value("${application.name}")
    private String applicationName;

    @Value("${test.environment:u2}")
    private String environmentName;

    public Map<String, Object> resolveFasitProperties() {
        Map<String, Object> properties = new HashMap<>();

        DataSource dataSource = fasitService.find("dollyDB", DATASOURCE, environmentName, applicationName, FSS, DataSource.class);
        properties.put("spring.datasource.url", dataSource.getUrl());
        properties.put("spring.datasource.username", dataSource.getUsername());
        properties.put("spring.datasource.password", fasitService.findSecret(dataSource.getPasswordUrl()));

        Credentials testEnvCredentials = fasitService.find("srvdolly-test-env", CREDENTIAL, environmentName, applicationName, FSS, Credentials.class);
        properties.put("credentials.testEnv.username", testEnvCredentials.getUsername());
        properties.put("credentials.testEnv.password", fasitService.findSecret(testEnvCredentials.getPasswordUrl()));

        Credentials preprodEnvCredentials = fasitService.find("srvdolly-preprod-env", CREDENTIAL, environmentName, applicationName, FSS, Credentials.class);
        properties.put("credentials.preprodEnv.username", preprodEnvCredentials.getUsername());
        properties.put("credentials.preprodEnv.password", fasitService.findSecret(preprodEnvCredentials.getPasswordUrl()));

        return properties;

    }
}