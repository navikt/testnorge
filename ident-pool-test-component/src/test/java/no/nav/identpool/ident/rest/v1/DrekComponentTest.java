package no.nav.identpool.ident.rest.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;

public class DrekComponentTest extends ComponentTestbase {

    @Test
    public void registrerFinnesISkd() throws URISyntaxException {
        URI uri = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_FINNES_HOS_SKD)
                .addParameter("personidentifikator", "711200510101")
                .addParameter("foedselsdato", "2000-12-31")
                .build();

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, lagHttpEntity(), ApiResponse.class);

        assertThat(apiResponseResponseEntity, is(notNullValue()));
    }

}
