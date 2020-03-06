package no.nav.registre.spion.consumer.rs;

import no.nav.registre.spion.consumer.rs.response.HodejegerenResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Mock
    private RestTemplate restTemplate;

    private HodejegerenConsumer hodejegerenConsumer;

    private static final String SERVERURL="http://localhost:0/testnorge-hodejegeren/api";
    private static final long AVSPILLERGRUPPEID = 1;
    private static final String MILJOE = "x2";
    private static final String IDENT = "123";
    private UriTemplate hentPersondataTilIdentUri;

    @Before
    public void setup(){
        hodejegerenConsumer = new HodejegerenConsumer(restTemplate, SERVERURL);
        hentPersondataTilIdentUri = new UriTemplate(SERVERURL
                +  "/v1/persondata?ident={ident}&miljoe={miljoe}");

    }

    @Test
    public void hentPersondataTilIdent() {

        HodejegerenResponse persondata = new HodejegerenResponse(
                IDENT,
                "FORNAVN",
                "MELLOMNAVN",
                "ETTERNAVN",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "");


        ResponseEntity<HodejegerenResponse> response = new ResponseEntity<>(persondata, HttpStatus.OK);

        var getRequest = RequestEntity.get(hentPersondataTilIdentUri.expand(IDENT, MILJOE)).build();

        when(restTemplate.exchange(getRequest, HodejegerenResponse.class))
                .thenReturn(response);

        HodejegerenResponse resultat = hodejegerenConsumer.hentPersondataTilIdent(IDENT, MILJOE);

        assertThat(resultat).isEqualTo(persondata);
    }

}
