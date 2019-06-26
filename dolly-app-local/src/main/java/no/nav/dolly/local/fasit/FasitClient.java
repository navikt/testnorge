package no.nav.dolly.local.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.DATASOURCE;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.DataSource;
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

        DataSource dataSource = fasitService.find("dollyDB", DATASOURCE, environmentName, applicationName, FSS, DataSource.class);
        properties.put("spring.datasource.url", dataSource.getUrl());
        properties.put("spring.datasource.username", dataSource.getUsername());
        properties.put("spring.datasource.password", fasitService.findSecret(dataSource.getPasswordUrl()));

        return properties;

    }
}