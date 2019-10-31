package no.nav.registre.aareg.service;

import static no.nav.registre.aareg.consumer.ws.AaregWsConsumer.STATUS_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.aareg.consumer.rs.AaregRestConsumer;
import no.nav.registre.aareg.consumer.rs.TpsfConsumer;
import no.nav.registre.aareg.consumer.rs.responses.MiljoerResponse;
import no.nav.registre.aareg.consumer.ws.AaregWsConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;

@RunWith(MockitoJUnitRunner.class)
public class AaregServiceTest {

    @Mock
    private AaregWsConsumer aaregWsConsumer;

    @Mock
    private AaregRestConsumer aaregRestConsumer;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private AaregService aaregService;

    private RsAaregOpprettRequest rsAaregOpprettRequest;
    private RsAaregOppdaterRequest rsAaregOppdaterRequest;
    private String ident = "01010101010";
    private String miljoe = "t1";
    private MiljoerResponse miljoerResponse;

    @Before
    public void setUp() {
        rsAaregOpprettRequest = new RsAaregOpprettRequest();
        rsAaregOppdaterRequest = new RsAaregOppdaterRequest();
        miljoerResponse = new MiljoerResponse(Collections.singletonList(miljoe));
    }

    @Test
    public void shouldOppretteArbeidsforhold() {
        aaregService.opprettArbeidsforhold(rsAaregOpprettRequest);
        verify(aaregWsConsumer).opprettArbeidsforhold(rsAaregOpprettRequest);
    }

    @Test
    public void shouldOppdatereArbeidsforhold() {
        aaregService.oppdaterArbeidsforhold(rsAaregOppdaterRequest);
        verify(aaregWsConsumer).oppdaterArbeidsforhold(rsAaregOppdaterRequest);
    }

    @Test
    public void shouldHenteArbeidsforhold() {
        aaregService.hentArbeidsforhold(ident, miljoe);
        verify(aaregRestConsumer).hentArbeidsforhold(ident, miljoe);
    }

    @Test
    public void shouldSletteArbeidsforhold() {
        var arbeidsforhold = buildArbeidsforhold();
        Map<String, String> status = new HashMap<>();
        status.put(miljoe, STATUS_OK);
        when(aaregRestConsumer.hentArbeidsforhold(ident, miljoe)).thenReturn(ResponseEntity.ok(Collections.singletonList(arbeidsforhold)));
        when(aaregWsConsumer.oppdaterArbeidsforhold(any())).thenReturn(status);

        var result = aaregService.slettArbeidsforhold(ident, Collections.singletonList(miljoe));

        assertThat(result.getStatusPerMiljoe().get(miljoe), equalTo(STATUS_OK));

        verify(aaregRestConsumer).hentArbeidsforhold(ident, miljoe);
        verify(aaregWsConsumer).oppdaterArbeidsforhold(any());
    }

    private Map buildArbeidsforhold() {
        Map<String, Object> arbeidsforhold = new HashMap<>();
        arbeidsforhold.put("arbeidsforholdId", "y6LJXvtsU57l2sTU");
        arbeidsforhold.put("navArbeidsforholdId", 3053173);

        Map<String, Object> arbeidstaker = new HashMap<>();
        arbeidstaker.put("offentligIdent", "16018809048");
        arbeidstaker.put("aktoerId", "1675247299346");
        arbeidstaker.put("type", "Person");
        arbeidsforhold.put("arbeidstaker", arbeidstaker);

        Map<String, Object> arbeidsgiver = new HashMap<>();
        arbeidsgiver.put("organisasjonsnummer", "874623512");
        arbeidsgiver.put("type", "Organisasjon");
        arbeidsforhold.put("arbeidsgiver", arbeidsgiver);

        arbeidsforhold.put("type", "ordinaertArbeidsforhold");

        Map<String, Object> opplysningspliktig = new HashMap<>();
        opplysningspliktig.put("organisasjonsnummer", "970490361");
        opplysningspliktig.put("type", "Organisasjon");
        arbeidsforhold.put("opplysningspliktig", opplysningspliktig);

        Map<String, Object> ansettelsesperiode = new HashMap<>();
        Map<String, Object> periode = new HashMap<>();
        periode.put("fom", "2012-10-14");
        ansettelsesperiode.put("periode", periode);
        arbeidsforhold.put("ansettelsesperiode", ansettelsesperiode);

        List<Object> arbeidsavtaler = new ArrayList<>();
        Map<String, Object> arbeidsavtale = new HashMap<>();
        arbeidsavtale.put("yrke", "8279102");
        arbeidsavtaler.add(arbeidsavtale);
        arbeidsforhold.put("arbeidsavtaler", arbeidsavtaler);

        return arbeidsforhold;
    }
}