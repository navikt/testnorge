package no.nav.dolly.regression.scenarios.rest.testgruppeBestilling;

import no.nav.dolly.regression.scenarios.rest.RestTestBase;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

public abstract class TestgruppeBestillingTestCaseBase extends RestTestBase {

    private static final String STANDARD_TPSF_HOST_URL = "https://localhost:8080";
    private static final String TPSF_HOST_PROPERTY_NAME = "tpsfServerUrl";

    @Mock
    protected RestTemplate tpsRestTemplate;

    protected String getEndpointUrl(Long gruppeId) {
        return "/api/v1/gruppe/" + gruppeId + "/bestilling";
    }
}
