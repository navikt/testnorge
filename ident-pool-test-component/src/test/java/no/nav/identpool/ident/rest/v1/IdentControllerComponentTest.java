package no.nav.identpool.ident.rest.v1;

import static no.nav.identpool.util.RestUtil.lagEnkelHttpEntity;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import org.apache.http.client.utils.URIBuilder;
import org.junit.After;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;

public class IdentControllerComponentTest extends ComponentTestbase {


    @Test
    public void hentLedigFnr() throws URISyntaxException {
        populerDatabaseMedTestidenter();
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_HENT)
                .addParameter("antall", "1")
                .addParameter("identtype", "FNR");

        ResponseEntity<String[]> fnr = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagEnkelHttpEntity(), String[].class);

        assertThat(fnr.getBody(), is(notNullValue()));
        assertThat(fnr.getBody().length, is(1));
    }

    @Test
    public void hentLedigDnr() throws URISyntaxException {
        populerDatabaseMedTestidenter();
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_HENT)
                .addParameter("antall", "1")
                .addParameter("identtype", "DNR");

        ResponseEntity<String[]> fnr = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagEnkelHttpEntity(), String[].class);

        assertThat(fnr.getBody(), is(notNullValue()));
        assertThat(fnr.getBody().length, is(1));
    }

    @Test
    public void skalFeileNaarUgyldigIdenttypeBrukes() throws URISyntaxException {
        populerDatabaseMedTestidenter();
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_HENT)
                .addParameter("antall", "1")
                .addParameter("identtype", "buksestørrelse");

        ResponseEntity<ApiError> apiErrorResponseEntity = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagEnkelHttpEntity(), ApiError.class);

        assertThat(apiErrorResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    public void populerDatabaseMedTestidenter() {
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
                        .personidentifikator("50108000398")
                        .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 10))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.FNR)
                        .personidentifikator("10108000399")
                        .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 10))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.DNR)
                        .personidentifikator("50108000399")
                        .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1991, 1, 1))
                        .build()
        ));
    }

    @After
    public void clearDatabase() {
        identRepository.deleteAll();
    }
}
