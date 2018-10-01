package no.nav.identpool.ident.rest.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import org.apache.http.client.utils.URIBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;

public class IdentpoolControllerComponentTest extends ComponentTestbase {

    @Test
    public void hentLedigFnr() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL)
                .addParameter("antall", "1")
                .addParameter("identtype", "FNR");

        ResponseEntity<String[]> fnr = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, lagHttpEntity(false), String[].class);

        assertThat(fnr.getBody(), is(notNullValue()));
        assertThat(fnr.getBody().length, is(1));
    }

    @Test
    public void hentLedigDnr() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL)
                .addParameter("antall", "1")
                .addParameter("identtype", "DNR");

        ResponseEntity<String[]> fnr = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, lagHttpEntity(false), String[].class);

        assertThat(fnr.getBody(), is(notNullValue()));
        assertThat(fnr.getBody().length, is(1));
    }

    @Test
    public void skalFeileNaarUgyldigIdenttypeBrukes() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL)
                .addParameter("antall", "1")
                .addParameter("identtype", "buksestørrelse");

        ResponseEntity<ApiError> apiErrorResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, lagHttpEntity(false), ApiError.class);

        assertThat(apiErrorResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void markerIBrukPaaIdentAlleredeIbruk() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/bruk")
                .addParameter("personidentifikator", "11108000327")
                .addParameter("bruker", "TesterMcTestFace");

        ResponseEntity<ApiError> apiErrorResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, lagHttpEntity(false), ApiError.class);

        assertThat(apiErrorResponseEntity.getStatusCode(), is(HttpStatus.GONE));

    }

    @Test
    public void sjekkOmLedigIdentErLedig() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/ledig")
                .addParameter("personidentifikator", "10108000398");

        ResponseEntity<Boolean> apiResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagHttpEntity(false), Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(true));

    }

    @Test
    public void sjekkOmUledigIdentErLedig() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/ledig")
                .addParameter("personidentifikator", "11108000327");

        ResponseEntity<Boolean> apiResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagHttpEntity(false), Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(false));

    }

    @Test
    public void eksistererIkkeIDbOgLedigITps() throws URISyntaxException {
        assertThat(identRepository.findTopByPersonidentifikator("20018049946"), is(nullValue()));

        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/ledig")
                .addParameter("personidentifikator", "20018049946");

        ResponseEntity<Boolean> apiResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagHttpEntity(false), Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(true));
        assertThat(identRepository.findTopByPersonidentifikator("20018049946").getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG));
    }

    @Test
    public void lesIdenterTest() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL)
                .addParameter("personidentifikator", "10108000398");

        ResponseEntity<IdentEntity> apiResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagHttpEntity(false), IdentEntity.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(IdentEntity.builder()
                .identtype(Identtype.FNR)
                .personidentifikator("10108000398")
                .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                .finnesHosSkatt("0")
                .foedselsdato(LocalDate.of(1980, 10, 10))
                .build()
        ));

    }

    @Before
    public void populerDatabaseMedTestidenter() {
        identRepository.deleteAll();
        identRepository.saveAll(Arrays.asList(
                IdentEntity.builder()
                        .identtype(Identtype.FNR)
                        .personidentifikator("10108000398")
                        .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 10))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.DNR)
                        .personidentifikator("50108000381")
                        .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 20))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.FNR)
                        .personidentifikator("11108000327")
                        .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 11))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.DNR)
                        .personidentifikator("12108000366")
                        .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 12))
                        .build()
        ));
    }

    @After
    public void clearDatabase() {
        identRepository.deleteAll();
    }
}
