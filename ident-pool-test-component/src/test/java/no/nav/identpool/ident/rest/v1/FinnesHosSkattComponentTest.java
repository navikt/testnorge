package no.nav.identpool.ident.rest.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import org.apache.http.client.utils.URIBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;

public class FinnesHosSkattComponentTest extends ComponentTestbase {

    private static final String DNR = "50108000381";
    private static final String NYTT_DNR = "50058000393";
    private static final String FNR = "10108000398";

    @Test
    public void registrerFinnesISkdUtenOidc() throws URISyntaxException {
        URI uri = new URIBuilder(FINNESHOSSKATT_V1_BASEURL).build();

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, lagHttpEntity(false, new FinnesHosSkattRequest(DNR)), ApiResponse.class);
        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void registrerFnrFinnesISkdMedGyldigOidc() throws URISyntaxException {
        URI uri = new URIBuilder(FINNESHOSSKATT_V1_BASEURL).build();

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, lagHttpEntity(true, new FinnesHosSkattRequest(FNR)), ApiResponse.class);

        //skal feile siden endepunktet kun skal ta DNR
        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void registrerFinnesISkdOgIdentpoolMedGyldigOidc() throws URISyntaxException {
        URI uri = new URIBuilder(FINNESHOSSKATT_V1_BASEURL).build();

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, lagHttpEntity(true, new FinnesHosSkattRequest(DNR)), ApiResponse.class);

        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.OK));

        assertThat(identRepository.findTopByPersonidentifikator(DNR).getFinnesHosSkatt(), is("1"));
        assertThat(identRepository.findTopByPersonidentifikator(DNR).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
    }

    @Test
    public void registrerFinnesISkdMenIkkeIIdentpoolMedGyldigOidc() throws URISyntaxException {
        URI uri = new URIBuilder(FINNESHOSSKATT_V1_BASEURL).build();

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, lagHttpEntity(true, new FinnesHosSkattRequest(NYTT_DNR)), ApiResponse.class);

        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.OK));

        assertThat(identRepository.findTopByPersonidentifikator(NYTT_DNR).getFinnesHosSkatt(), is("1"));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_DNR).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
    }

    @Test
    public void registrerFinnesISkdMedUgyldigOidc() throws URISyntaxException {
        URI uri = new URIBuilder(FINNESHOSSKATT_V1_BASEURL).build();

        HttpHeaders httpEntityWithInvalidToken = new HttpHeaders();
        httpEntityWithInvalidToken.add(HttpHeaders.CONTENT_TYPE, "application/json");
        httpEntityWithInvalidToken.add(HttpHeaders.AUTHORIZATION, "Bearer eyJrawJEGxsdERasjdhIKKEjshjsdhETasnmbhfvTOKEN");

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, new HttpEntity(DNR, httpEntityWithInvalidToken), ApiResponse.class);

        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Before
    public void populerDatabaseMedTestidenter() {
        identRepository.deleteAll();
        identRepository.save(
                IdentEntity.builder()
                        .identtype(Identtype.FNR)
                        .kjoenn(Kjoenn.MANN)
                        .personidentifikator(DNR)
                        .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 10))
                        .build()
        );
    }

    @After
    public void clearDatabase() {
        identRepository.deleteAll();
    }
}

