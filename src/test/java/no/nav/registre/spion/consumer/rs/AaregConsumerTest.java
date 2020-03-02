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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class AaregConsumerTest {

    @Mock
    private RestTemplate restTemplate;

    private AaregConsumer aaregConsumer;

    private String serverUrl;
    private long avspillergruppeId;
    private String miljoe;
    private String ident;
    private UriTemplate hentAlleIdenterMedArbeidsforholdUri;
    private UriTemplate hentArbeidsforholdtilIdentUri;

    @Before
    public void setup(){
        serverUrl = "http://localhost:0/testnorge-aareg/api";
        aaregConsumer = new AaregConsumer(restTemplate, serverUrl);
        hentAlleIdenterMedArbeidsforholdUri = new UriTemplate(serverUrl
                +  "/v1/ident/avspillergruppe/{avspillergruppeId}?miljoe={miljoe}");

        hentArbeidsforholdtilIdentUri = new UriTemplate(serverUrl
                + "/v1/ident/{ident}?miljoe={miljoe}");
        avspillergruppeId = 1;
        miljoe = "x2";
        ident = "123";
    }


    @Test
    public void hentAlleIdenterMedArbeidsforhold() {
        List<String> data = new ArrayList<>();
        data.add(ident);

        ResponseEntity<List<String>> response = new ResponseEntity<>(data, HttpStatus.OK);

        var getRequest = RequestEntity.get(hentAlleIdenterMedArbeidsforholdUri.expand(avspillergruppeId, miljoe)).build();

        when(restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<String>>(){}))
                .thenReturn(response);

        List<String> fnrs = aaregConsumer.hentAlleIdenterMedArbeidsforhold(avspillergruppeId, miljoe);

        assertTrue(fnrs.contains(ident));
    }

    @Test
    public void hentArbeidsforholdTilIdent() {

        List<AaregResponse> arbeidsforhold = new ArrayList<>();
        arbeidsforhold.add(new AaregResponse(
                1234,
                "",
                new Arbeidstaker("",ident,""),
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

        var getRequest = RequestEntity.get(hentArbeidsforholdtilIdentUri.expand(ident, miljoe)).build();

        when(restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<AaregResponse>>(){}))
                .thenReturn(response);

        List<AaregResponse> result = aaregConsumer.hentArbeidsforholdTilIdent(ident, miljoe);

        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getArbeidstaker().getOffentligIdent().equals(ident));
    }
}
