package no.nav.identpool.rs.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.rs.v1.support.ApiResponse;
import no.nav.identpool.rs.v1.support.IdentRequest;

@DisplayName("POST /api/v1/finneshosskatt")
class FinnesHosSkattComponentTest extends ComponentTestbase {

    private static final String DNR = "50108000381";
    private static final String NYTT_DNR = "50058000393";
    private static final String FNR = "10108000398";

    private URI ROOT_URI;

    @BeforeEach
    void populerDatabaseMedTestidenter() throws URISyntaxException {
        ROOT_URI = new URIBuilder(FINNESHOSSKATT_V1_BASEURL).build();

        identRepository.deleteAll();
        identRepository.save(
                createIdentEntity(Identtype.FNR, DNR, Rekvireringsstatus.LEDIG, 10)
        );
    }

    @AfterEach
    void clearDatabase() {
        identRepository.deleteAll();
    }

    @Test
    void registrerFnrFinnesISkdMedGyldigOidc() {
        IdentRequest request = new IdentRequest(FNR);

        ResponseEntity<ApiResponse> apiResponseResponseEntity = doPostRequest(ROOT_URI, createBodyEntity(request, true), ApiResponse.class);

        //skal feile siden endepunktet kun skal ta DNR
        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void registrerFinnesISkdOgIdentpoolMedGyldigOidc() {
        IdentRequest request = new IdentRequest(DNR);

        ResponseEntity<ApiResponse> apiResponseResponseEntity = doPostRequest(ROOT_URI, createBodyEntity(request, true), ApiResponse.class);

        assertOK(apiResponseResponseEntity, DNR);
    }

    @Test
    void registrerFinnesISkdMenIkkeIIdentpoolMedGyldigOidc() {
        IdentRequest request = new IdentRequest(NYTT_DNR);

        ResponseEntity<ApiResponse> apiResponseResponseEntity = doPostRequest(ROOT_URI, createBodyEntity(request, true), ApiResponse.class);

        assertOK(apiResponseResponseEntity, NYTT_DNR);
    }

    private void assertOK(ResponseEntity<ApiResponse> apiResponseResponseEntity, String dnr) {
        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.OK));

        assertTrue(identRepository.findTopByPersonidentifikator(dnr).isFinnesHosSkatt());
        assertThat(identRepository.findTopByPersonidentifikator(dnr).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
    }
}