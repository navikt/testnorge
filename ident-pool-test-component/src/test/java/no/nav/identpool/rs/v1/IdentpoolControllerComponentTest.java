package no.nav.identpool.rs.v1;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.repository.IdentEntity;
import no.nav.identpool.util.PersonidentifikatorUtil;

public class IdentpoolControllerComponentTest extends ComponentTestbase {

    private static final String FNR_LEDIG = "10108000398";
    private static final String DNR_LEDIG = "50108000381";
    private static final String FNR_IBRUK = "11108000327";
    private static final String NYTT_FNR_LEDIG = "20018049946";

    @Test
    public void hentLedigFnr() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL);
        String body = "{\"antall\":\"1\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\" }";

        ResponseEntity<String[]> identListe = testRestTemplate.exchange(
                uriBuilder.build(),
                HttpMethod.POST,
                httpEntityBuilder.withBody(body).build(),
                String[].class);

        assertThat(identListe.getBody(), is(notNullValue()));
        assertThat(PersonidentifikatorUtil.getPersonidentifikatorType(identListe.getBody()[0]), is(Identtype.FNR));
        assertThat(identListe.getBody().length, is(1));
    }

    @Test
    public void hentLedigDnr() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL);
        String body = "{\"antall\":\"1\", \"identtype\":\"DNR\",\"foedtEtter\":\"1900-01-01\" }";

        ResponseEntity<String[]> identListe = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, httpEntityBuilder.withBody(body).build(), String[].class);

        assertThat(identListe.getBody(), is(notNullValue()));
        assertThat(PersonidentifikatorUtil.getPersonidentifikatorType(identListe.getBody()[0]), is(Identtype.DNR));
        assertThat(identListe.getBody().length, is(1));
    }

    @Test
    public void hentLedigIdent() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL);
        String body = "{\"antall\":\"3\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\",\"foedtFoer\":\"1950-01-01\"}";

        ResponseEntity<String[]> identListe = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, httpEntityBuilder.withBody(body).build(), String[].class);

        assertThat(identListe.getBody(), is(notNullValue()));
        assertThat(identListe.getBody().length, is(3));

        long countDb = identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(
                LocalDate.of(1900, 1, 1),
                LocalDate.of(1950, 1, 1),
                Identtype.FNR,
                Rekvireringsstatus.I_BRUK);

        assertThat(countDb, is(3L));
    }

    @Test
    public void hentForMangeIdenterSomIkkeFinnesIDatabasen() throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL);
        String body = "{\"antall\":\"200\", \"foedtEtter\":\"1900-01-01\"}";

        ResponseEntity<ForFaaLedigeIdenterException> identListe = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, httpEntityBuilder.withBody(body).build(), ForFaaLedigeIdenterException.class);

        assertThat(identListe.getStatusCode(), is(HttpStatus.SERVICE_UNAVAILABLE));
    }

    @Test
    public void skalFeileNaarUgyldigIdenttypeBrukes() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL);
        String body = "{\"antall\":\"1\", \"identtype\":\"buksestoerrelse\" }";

        ResponseEntity<ApiError> apiErrorResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, httpEntityBuilder.withBody(body).build(), ApiError.class);

        assertThat(apiErrorResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void markerIBrukPaaIdentAlleredeIbruk() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/bruk");
        String body = "{\"personidentifikator\":\"" + FNR_IBRUK + "\", \"rekvirertAv\":\"TesterMcTestFace\" }";

        ResponseEntity<ApiError> apiErrorResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, httpEntityBuilder.withBody(body).build(), ApiError.class);

        assertThat(apiErrorResponseEntity.getStatusCode(), is(HttpStatus.GONE));

    }

    @Test
    public void markerEksisterendeLedigIdentIBruk() throws URISyntaxException {
        assertThat(identRepository.findTopByPersonidentifikator(FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG));

        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/bruk");
        String body = "{\"personidentifikator\":\"" + FNR_LEDIG + "\", \"rekvirertAv\":\"TesterMcTestFace\" }";

        ResponseEntity<ApiResponse> apiResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, httpEntityBuilder.withBody(body).build(), ApiResponse.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(identRepository.findTopByPersonidentifikator(FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));

    }

    @Test
    public void markerNyLedigIdentIBruk() throws URISyntaxException {
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG), is(nullValue()));

        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/bruk");
        String body = "{\"personidentifikator\":\"" + NYTT_FNR_LEDIG + "\", \"rekvirertAv\":\"TesterMcTestFace\" }";

        ResponseEntity<ApiResponse> apiResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.POST, httpEntityBuilder.withBody(body).build(), ApiResponse.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getIdenttype(), is(Identtype.FNR));

    }

    @Test
    public void sjekkOmLedigIdentErLedig() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/ledig");

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", FNR_LEDIG);

        HttpEntity httpEntity = httpEntityBuilder.withHeaders(headers).build();

        ResponseEntity<Boolean> apiResponseEntity = testRestTemplate.exchange(
                uriBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(true));

    }

    @Test
    public void sjekkOmUledigIdentErLedig() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/ledig");

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", FNR_IBRUK);

        HttpEntity httpEntity = httpEntityBuilder.withHeaders(headers).build();

        ResponseEntity<Boolean> apiResponseEntity = testRestTemplate.exchange(
                uriBuilder.build(),
                HttpMethod.GET,
                httpEntity,
                Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(false));

    }

    @Test
    public void eksistererIkkeIDbOgLedigITps() throws URISyntaxException {
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG), is(nullValue()));

        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + "/ledig");

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", NYTT_FNR_LEDIG);

        ResponseEntity<Boolean> apiResponseEntity = testRestTemplate.exchange(
                uriBuilder.build(),
                HttpMethod.GET,
                httpEntityBuilder.withHeaders(headers).build(),
                Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(true));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG));
    }

    @Test
    public void lesIdenterTest() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL);

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", FNR_LEDIG);

        ResponseEntity<IdentEntity> apiResponseEntity = testRestTemplate.exchange(
                uriBuilder.build(),
                HttpMethod.GET,
                httpEntityBuilder.withHeaders(headers).build(),
                IdentEntity.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(IdentEntity.builder()
                .identtype(Identtype.FNR)
                .personidentifikator(FNR_LEDIG)
                .kjoenn(Kjoenn.MANN)
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
                        .personidentifikator(FNR_LEDIG)
                        .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                        .finnesHosSkatt("0")
                        .kjoenn(Kjoenn.MANN)
                        .foedselsdato(LocalDate.of(1980, 10, 10))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.DNR)
                        .personidentifikator(DNR_LEDIG)
                        .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                        .finnesHosSkatt("0")
                        .kjoenn(Kjoenn.MANN)
                        .foedselsdato(LocalDate.of(1980, 10, 20))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.FNR)
                        .personidentifikator(FNR_IBRUK)
                        .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                        .finnesHosSkatt("0")
                        .kjoenn(Kjoenn.MANN)
                        .foedselsdato(LocalDate.of(1980, 10, 11))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.DNR)
                        .personidentifikator("12108000366")
                        .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                        .finnesHosSkatt("0")
                        .kjoenn(Kjoenn.MANN)
                        .foedselsdato(LocalDate.of(1980, 10, 12))
                        .build()
        ));
    }

    @After
    public void clearDatabase() {
        identRepository.deleteAll();
    }
}
