package no.nav.dolly.regression.scenarios.rest.testgruppeBestilling;

import no.nav.dolly.regression.scenarios.rest.RestTestBase;

public abstract class TestgruppeBestillingTestCaseBase extends RestTestBase {

    protected String getEndpointUrl(Long gruppeId){
        return "/api/v1/" + gruppeId + "/bestilling";
    }
}
