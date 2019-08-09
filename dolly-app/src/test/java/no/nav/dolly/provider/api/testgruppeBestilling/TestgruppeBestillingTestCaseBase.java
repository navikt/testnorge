package no.nav.dolly.provider.api.testgruppeBestilling;

import no.nav.dolly.provider.RestTestBase;

abstract class TestgruppeBestillingTestCaseBase extends RestTestBase {
    String getEndpointUrl(Long gruppeId) {
        return "/api/v1/gruppe/" + gruppeId + "/bestilling";
    }
}
