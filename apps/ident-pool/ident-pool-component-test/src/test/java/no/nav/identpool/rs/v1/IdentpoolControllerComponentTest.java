package no.nav.identpool.rs.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;

import no.nav.identpool.domain.postgres.Ident;
import no.nav.identpool.rs.v1.support.ApiError;
import no.nav.identpool.rs.v1.support.ApiResponse;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.util.PersonidentUtil;

class IdentpoolControllerComponentTest extends ComponentTestbase {

    private static final String FNR_LEDIG = "10108000398";
    private static final String DNR_LEDIG = "50108000381";
    private static final String FNR_IBRUK = "11108000327";
    private static final String NYTT_FNR_LEDIG = "20018049946";

    private URI ROOT_URI;
    private URI BRUK_URI;
    private URI BRUK_BATCH_URI;
    private URI LEDIG_URI;

    @BeforeEach
    void populerDatabaseMedTestidenter() throws URISyntaxException {
        ROOT_URI = new URIBuilder(IDENT_V1_BASEURL).build();
        BRUK_URI = new URIBuilder(IDENT_V1_BASEURL + "/bruk").build();
        BRUK_BATCH_URI = new URIBuilder(IDENT_V1_BASEURL + "/bruk/batch").build();
        LEDIG_URI = new URIBuilder(IDENT_V1_BASEURL + "/ledig").build();

        identRepository.deleteAll();
        identRepository.saveAll(Arrays.asList(
                createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10),
                createIdentEntity(Identtype.DNR, DNR_LEDIG, Rekvireringsstatus.LEDIG, 20),
                createIdentEntity(Identtype.FNR, FNR_IBRUK, Rekvireringsstatus.I_BRUK, 11),
                createIdentEntity(Identtype.DNR, "12108000366", Rekvireringsstatus.I_BRUK, 12)
        ));
    }

    @AfterEach
    void clearDatabase() {
        identRepository.deleteAll();
    }

    @Test
    void hentLedigFnr() {
        String body = "{\"antall\":\"1\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\" }";

        ResponseEntity<String[]> identListe = doPostRequest(ROOT_URI, createBodyEntity(body), String[].class);

        assertThat(identListe.getBody(), is(notNullValue()));
        assertThat(PersonidentUtil.getIdentType(identListe.getBody()[0]), is(Identtype.FNR));
        assertThat(identListe.getBody().length, is(1));
    }

    @Test
    void hentLedigDnr() {
        String body = "{\"antall\":\"2\", \"identtype\":\"DNR\",\"foedtEtter\":\"1900-01-01\" }";

        ResponseEntity<String[]> identListe = doPostRequest(ROOT_URI, createBodyEntity(body), String[].class);

        assertThat(identListe.getBody(), is(notNullValue()));
        assertThat(PersonidentUtil.getIdentType(identListe.getBody()[0]), is(Identtype.DNR));
        assertThat(identListe.getBody().length, is(2));
    }

    @Test
    void hentLedigIdent() {
        String body = "{\"antall\":\"3\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\",\"foedtFoer\":\"1950-01-01\"}";

        ResponseEntity<String[]> identListe = doPostRequest(ROOT_URI, createBodyEntity(body), String[].class);

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
    void hentForMangeIdenterSomIkkeFinnesIDatabasen() {
        String body = "{\"antall\":\"200\", \"foedtEtter\":\"1900-01-01\"}";

        ResponseEntity<ForFaaLedigeIdenterException> identListe = doPostRequest(ROOT_URI, createBodyEntity(body), ForFaaLedigeIdenterException.class);

        assertThat(identListe.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void skalFeileNaarUgyldigIdenttypeBrukes() {
        String body = "{\"antall\":\"1\", \"identtype\":\"buksestoerrelse\" }";

        ResponseEntity<ApiError> apiErrorResponseEntity = doPostRequest(ROOT_URI, createBodyEntity(body), ApiError.class);

        assertThat(apiErrorResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void markerIBrukPaaIdentAlleredeIBruk() {
        String body = "{\"personidentifikator\":\"" + FNR_IBRUK + "\", \"bruker\":\"TesterMcTestFace\" }";

        ResponseEntity<ApiError> apiErrorResponseEntity = doPostRequest(BRUK_URI, createBodyEntity(body), ApiError.class);

        assertThat(apiErrorResponseEntity.getStatusCode(), is(HttpStatus.CONFLICT));

    }

    @Test
    void markerEksisterendeLedigIdentIBruk() {
        assertThat(identRepository.findTopByPersonidentifikator(FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG));

        String body = "{\"personidentifikator\":\"" + FNR_LEDIG + "\", \"bruker\":\"TesterMcTestFace\" }";

        ResponseEntity<ApiResponse> apiResponseEntity = doPostRequest(BRUK_URI, createBodyEntity(body), ApiResponse.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(identRepository.findTopByPersonidentifikator(FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));

    }

    @Test
    void markerNyLedigIdentIBruk() {
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG), is(nullValue()));

        String body = "{\"personidentifikator\":\"" + NYTT_FNR_LEDIG + "\", \"bruker\":\"TesterMcTestFace\" }";

        ResponseEntity<ApiResponse> apiResponseEntity = doPostRequest(BRUK_URI, createBodyEntity(body), ApiResponse.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getIdenttype(), is(Identtype.FNR));

    }

    @Test
    void markerNyLedigIdentIBrukBatch() {
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG), is(nullValue()));

        String body = "{\"personidentifikatorer\":[\"" + NYTT_FNR_LEDIG + "\"], \"bruker\":\"TesterMcTestFace\" }";

        ResponseEntity<ApiResponse> apiResponseEntity = doPostRequest(BRUK_BATCH_URI, createBodyEntity(body), ApiResponse.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getIdenttype(), is(Identtype.FNR));
    }

    @Test
    void sjekkOmLedigIdentErLedig() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", FNR_LEDIG);

        ResponseEntity<Boolean> apiResponseEntity = doGetRequest(LEDIG_URI, createHeaderEntity(headers), Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(true));

    }

    @Test
    void sjekkOmUledigIdentErLedig() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", FNR_IBRUK);

        ResponseEntity<Boolean> apiResponseEntity = doGetRequest(LEDIG_URI, createHeaderEntity(headers), Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(false));

    }

    @Test
    void eksistererIkkeIDbOgLedigITps() {
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG), is(nullValue()));
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", NYTT_FNR_LEDIG);

        ResponseEntity<Boolean> apiResponseEntity = doGetRequest(LEDIG_URI, createHeaderEntity(headers), Boolean.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(apiResponseEntity.getBody(), is(true));
    }

    @Test
    void lesIdenterTest() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", FNR_LEDIG);

        ResponseEntity<Ident> apiResponseEntity = doGetRequest(ROOT_URI, createHeaderEntity(headers), Ident.class);

        assertThat(apiResponseEntity.getStatusCode(), is(HttpStatus.OK));
        Ident expected = createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10);
        assertThat(apiResponseEntity.getBody(), is(expected));
    }
}
