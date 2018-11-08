package no.nav.identpool.rs.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;

public class FinnesHosSkattComponentTest extends ComponentTestbase {

    private static final String DNR = "50108000381";
    private static final String NYTT_DNR = "50058000393";
    private static final String FNR = "10108000398";

    private URI ROOT_URI;

    @Before
    public void populerDatabaseMedTestidenter() throws URISyntaxException {
        ROOT_URI = new URIBuilder(FINNESHOSSKATT_V1_BASEURL).build();

        identRepository.deleteAll();
        identRepository.save(
                createIdentEntity(Identtype.FNR, DNR, Rekvireringsstatus.LEDIG, 10)
        );
    }

    @After
    public void clearDatabase() {
        identRepository.deleteAll();
    }

    @Test
    public void registrerFinnesISkdUtenOidc() {
        IdentRequest request = new IdentRequest(DNR);

        ResponseEntity<ApiResponse> apiResponseResponseEntity = doPostRequest(ROOT_URI, createBodyEntity(request, false), ApiResponse.class);

        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void registrerFnrFinnesISkdMedGyldigOidc() {
        IdentRequest request = new IdentRequest(FNR);

        ResponseEntity<ApiResponse> apiResponseResponseEntity = doPostRequest(ROOT_URI, createBodyEntity(request, true), ApiResponse.class);

        //skal feile siden endepunktet kun skal ta DNR
        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void registrerFinnesISkdOgIdentpoolMedGyldigOidc() {
        IdentRequest request = new IdentRequest(DNR);

        ResponseEntity<ApiResponse> apiResponseResponseEntity = doPostRequest(ROOT_URI, createBodyEntity(request, true), ApiResponse.class);

        assertOK(apiResponseResponseEntity, DNR);
    }

    @Test
    public void registrerFinnesISkdMenIkkeIIdentpoolMedGyldigOidc() {
        IdentRequest request = new IdentRequest(NYTT_DNR);

        ResponseEntity<ApiResponse> apiResponseResponseEntity = doPostRequest(ROOT_URI, createBodyEntity(request, true), ApiResponse.class);

        assertOK(apiResponseResponseEntity, NYTT_DNR);
    }

    @Test
    public void registrerFinnesISkdMedUgyldigOidc() {
        HttpHeaders httpEntityWithInvalidToken = new HttpHeaders();
        httpEntityWithInvalidToken.add(HttpHeaders.CONTENT_TYPE, "application/json");
        httpEntityWithInvalidToken.add(HttpHeaders.AUTHORIZATION, "Bearer eyJrawJEGxsdERasjdhIKKEjshjsdhETasnmbhfvTOKEN");

        ResponseEntity<ApiResponse> apiResponseResponseEntity = doPostRequest(ROOT_URI, new HttpEntity<>(DNR, httpEntityWithInvalidToken), ApiResponse.class);

        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private void assertOK(ResponseEntity<ApiResponse> apiResponseResponseEntity, String dnr) {
        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.OK));

        assertTrue(identRepository.findTopByPersonidentifikator(dnr).finnesHosSkatt());
        assertThat(identRepository.findTopByPersonidentifikator(dnr).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
    }
}