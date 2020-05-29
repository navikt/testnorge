package no.nav.registre.spion.service;

import no.nav.registre.spion.consumer.rs.AaregConsumer;
import no.nav.registre.spion.consumer.rs.HodejegerenConsumer;
import no.nav.registre.spion.consumer.rs.response.HodejegerenResponse;

import no.nav.registre.spion.consumer.rs.response.aareg.AaregResponse;
import no.nav.registre.spion.consumer.rs.response.aareg.Arbeidsgiver;
import no.nav.registre.spion.consumer.rs.response.aareg.Arbeidstaker;
import no.nav.registre.spion.consumer.rs.response.aareg.Sporingsinformasjon;
import no.nav.registre.spion.provider.rs.response.SyntetiserVedtakResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class SyntetiseringsServiceTest {

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private AaregConsumer aaregConsumer;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private static final long AVSPILLERGRUPPEID = 1;
    private static final String MILJOE = "x2";
    private static final String IDENT = "123";

    private HodejegerenResponse persondata;
    private List<AaregResponse> arbeidsforhold;


    @Before
    public void setup(){

        this.persondata = new HodejegerenResponse(
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

        this.arbeidsforhold = Collections.singletonList(new AaregResponse(
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
    }

    @Test
    public void shouldSyntetiserVedtak() {


        when(aaregConsumer.hentAlleIdenterMedArbeidsforhold(AVSPILLERGRUPPEID, MILJOE))
                .thenReturn(Collections.singletonList(IDENT));
        when(aaregConsumer.hentArbeidsforholdTilIdent(IDENT, MILJOE)).thenReturn(arbeidsforhold);
        when(hodejegerenConsumer.hentPersondataTilIdent(IDENT, MILJOE)).thenReturn(persondata);


        List<SyntetiserVedtakResponse> resultat = syntetiseringService.syntetiserVedtak(
                AVSPILLERGRUPPEID,
                MILJOE,
                1,
                LocalDate.now().minusMonths(18),
                LocalDate.now(),
                1);

        assertThat(resultat.get(0).getVedtak()).hasSize(1);
        assertThat(resultat.get(0).getIdentitetsnummer()).isEqualTo(IDENT);
    }

}
