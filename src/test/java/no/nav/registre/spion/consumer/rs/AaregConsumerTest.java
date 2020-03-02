package no.nav.registre.spion.consumer.rs;
import no.nav.registre.spion.consumer.rs.response.aareg.AaregResponse;
import no.nav.registre.spion.consumer.rs.response.aareg.Arbeidsgiver;
import no.nav.registre.spion.consumer.rs.response.aareg.Arbeidstaker;
import no.nav.registre.spion.consumer.rs.response.aareg.Sporingsinformasjon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class AaregConsumerTest {

    @Mock
    private RestTemplate restTemplate;

    private AaregConsumer aaregConsumer;

    private static final String SERVERURL="http://localhost:0/testnorge-aareg/api";
    private static final long AVSPILLERGRUPPEID = 1;
    private static final String MILJOE = "x2";
    private static final String IDENT = "123";
    private UriTemplate hentAlleIdenterMedArbeidsforholdUri;
    private UriTemplate hentArbeidsforholdtilIdentUri;

    @Before
    public void setup(){
        aaregConsumer = new AaregConsumer(restTemplate, SERVERURL);
        hentAlleIdenterMedArbeidsforholdUri = new UriTemplate(SERVERURL
                +  "/v1/ident/avspillergruppe/{avspillergruppeId}?miljoe={miljoe}");

        hentArbeidsforholdtilIdentUri = new UriTemplate(SERVERURL
                + "/v1/ident/{ident}?miljoe={miljoe}");
    }


    @Test
    public void hentAlleIdenterMedArbeidsforhold() {
        List<String> data = Collections.singletonList(IDENT);

        ResponseEntity<List<String>> response = new ResponseEntity<>(data, HttpStatus.OK);

        var getRequest = RequestEntity.get(hentAlleIdenterMedArbeidsforholdUri.expand(AVSPILLERGRUPPEID, MILJOE)).build();

        when(restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<String>>(){}))
                .thenReturn(response);

        List<String> fnrs = aaregConsumer.hentAlleIdenterMedArbeidsforhold(AVSPILLERGRUPPEID, MILJOE);

        assertThat(fnrs).contains(IDENT);
    }

    @Test
    public void hentArbeidsforholdTilIdent() {

        List<AaregResponse> arbeidsforhold = Collections.singletonList(new AaregResponse(
                1234,
                "",
                new Arbeidstaker("",IDENT,""),
                new Arbeidsgiver("organisasjon", "org_nr"),
                null,
                "",
                null,
                null,
                false,
                LocalDate.now(),
                LocalDate.now(),
                new Sporingsinformasjon()
        ));

        ResponseEntity<List<AaregResponse>> response = new ResponseEntity<>(arbeidsforhold, HttpStatus.OK);

        var getRequest = RequestEntity.get(hentArbeidsforholdtilIdentUri.expand(IDENT, MILJOE)).build();

        when(restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<AaregResponse>>(){}))
                .thenReturn(response);

        List<AaregResponse> result = aaregConsumer.hentArbeidsforholdTilIdent(IDENT, MILJOE);

        assertThat(result).hasSize(1).first().isEqualTo(arbeidsforhold.get(0));
    }
}
