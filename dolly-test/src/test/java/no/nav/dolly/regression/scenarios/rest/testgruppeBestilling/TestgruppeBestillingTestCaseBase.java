package no.nav.dolly.regression.scenarios.rest.testgruppeBestilling;

import no.nav.dolly.regression.scenarios.rest.RestTestBase;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

public abstract class TestgruppeBestillingTestCaseBase extends RestTestBase {
    
    protected String getEndpointUrl(Long gruppeId) {
        return "/api/v1/gruppe/" + gruppeId + "/bestilling";
    }
}
