package no.nav.identpool.ident.rest.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;

public class IdentControllerComponentTest extends ComponentTestbase {


    @Test
    public void hentLedigFnr() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_HENT)
                .addParameter("antall", "1")
                .addParameter("identtype", "FNR");

        ResponseEntity<String[]> fnr = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagHttpEntity(), String[].class);

        assertThat(fnr.getBody(), is(notNullValue()));
        assertThat(fnr.getBody().length, is(1));
    }

    @Test
    public void hentLedigDnr() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(IDENT_V1_BASEURL + OPERASJON_HENT)
                .addParameter("antall", "1")
                .addParameter("identtype", "DNR");

        ResponseEntity<String[]> fnr = testRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, lagHttpEntity(), String[].class);

        assertThat(fnr.getBody(), is(notNullValue()));
        assertThat(fnr.getBody().length, is(1));
    }
}
