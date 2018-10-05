package no.nav.registre.orkestratoren.fasit;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.BaseUrl;
import no.nav.freg.fasit.utils.domain.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static no.nav.freg.fasit.utils.domain.ResourceType.BASE_URL;
import static no.nav.freg.fasit.utils.domain.ResourceType.CREDENTIAL;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;


@Component
@Profile("fasit")
@RequiredArgsConstructor
public class FasitClient {

    @Autowired
    private FasitService fasitService;

    @Value("${application.name}")
    private String applicationName;
    @Value("${test.environment}")
    private String environmentName;

    public Map<String, Object> resolveFasitProperties() {
        Map<String, Object> properties = new HashMap<>();

        Credentials credentials = fasitService.find("orkestratorenCredentials", CREDENTIAL, environmentName, applicationName, FSS, Credentials.class);
        properties.put("spring.credentials.username", credentials.getUsername());
        properties.put("spring.credentials.password", fasitService.findSecret(credentials.getPasswordUrl()));

        return properties;
    }
}