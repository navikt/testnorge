package no.nav.identpool.ident.rest.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;

public class DrekComponentTest extends ComponentTestbase {

    @Test
    public void registrerFinnesISkdUtenOidc() throws URISyntaxException {
        URI uri = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_FINNES_HOS_SKD)
                .addParameter("personidentifikator", "711200510101")
                .addParameter("foedselsdato", "2000-12-31")
                .build();

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, lagHttpEntity(false), ApiResponse.class);
        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void registrerFinnesISkdMedGyldigOidc() throws URISyntaxException {
        URI uri = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_FINNES_HOS_SKD)
                .addParameter("personidentifikator", "711200510101")
                .addParameter("foedselsdato", "2000-12-31")
                .build();

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, lagHttpEntity(true), ApiResponse.class);

        assertThat(apiResponseResponseEntity, is(notNullValue()));
        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void registrerFinnesISkdMedUgyldigOidc() throws URISyntaxException {
        URI uri = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_FINNES_HOS_SKD)
                .addParameter("personidentifikator", "711200510101")
                .addParameter("foedselsdato", "2000-12-31")
                .build();

        HttpHeaders httpEntityWithInvalidToken = new HttpHeaders();
        httpEntityWithInvalidToken.add(HttpHeaders.CONTENT_TYPE, "application/json");
        httpEntityWithInvalidToken.add(HttpHeaders.AUTHORIZATION, "Bearer eyJrawJEGxsdERasjdhIKKEjshjsdhETasnmbhfvTOKEN");

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, new HttpEntity(httpEntityWithInvalidToken), ApiResponse.class);

        assertThat(apiResponseResponseEntity.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

}
