package no.nav.dolly.aareg;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@RunWith(MockitoJUnitRunner.class)
public class AaregClientTest {

    private static final String IDENT = "111111111111";
    private static final String ENV = "u2";
    private static final String ORGNUMMER = "222222222";
    private static final Integer NAV_ARBFORHOLD_ID = 333333333;

    @Mock
    private AaregWsConsumer aaregWsConsumer;

    @Mock
    private AaregRestConsumer aaregRestConsumer;

    @InjectMocks
    private AaregClient aaregClient;

    @Test
    public void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_OK() {

        when(aaregRestConsumer.readArbeidsforhold(IDENT, ENV)).thenReturn(ResponseEntity.ok(new Object[] {}));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder().build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        verify(aaregWsConsumer).opprettArbeidsforhold(any(RsAaregOpprettRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_lesKasterException() {

        when(aaregRestConsumer.readArbeidsforhold(IDENT, ENV)).thenThrow(new RuntimeException());

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder().build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        verify(aaregWsConsumer).opprettArbeidsforhold(any(RsAaregOpprettRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_arbeidsgiverHarOrgnummer() {

        when(aaregRestConsumer.readArbeidsforhold(IDENT, ENV)).thenReturn(ResponseEntity.ok(buildArbeidsforhold(true)));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder()
                .arbeidsgiver(RsOrganisasjon.builder().orgnummer(ORGNUMMER).build())
                .arbeidstaker(RsPersonAareg.builder().ident(IDENT).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request, TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        verify(aaregWsConsumer).oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_arbeidsgiverHarPersonnr() {

        when(aaregRestConsumer.readArbeidsforhold(IDENT, ENV)).thenReturn(ResponseEntity.ok(buildArbeidsforhold(false)));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder()
                .arbeidsgiver(RsAktoerPerson.builder().ident(IDENT).build())
                .arbeidstaker(RsPersonAareg.builder().ident(IDENT).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        verify(aaregWsConsumer).oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_sjekkReturStatus() {

        when(aaregRestConsumer.readArbeidsforhold(IDENT, ENV)).thenReturn(ResponseEntity.ok(buildArbeidsforhold(false)));
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        when(aaregWsConsumer.oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class))).thenReturn(status);

        BestillingProgress progress = new BestillingProgress();

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder()
                .arbeidsgiver(RsAktoerPerson.builder().ident(IDENT).build())
                .arbeidstaker(RsPersonAareg.builder().ident(IDENT).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request, TpsPerson.builder().hovedperson(IDENT).build(), progress);

        verify(aaregWsConsumer).oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class));
        assertThat(progress.getAaregStatus(), is(equalTo("u2: arbforhold=1$OK")));
    }

    private Object[] buildArbeidsforhold(boolean isOrgnummer) {

        Map<String, Object> arbeidstaker = new HashMap<>();
        arbeidstaker.put("offentligIdent", IDENT);
        Map<String, Object> arbeidsgiver = new HashMap<>();
        if (isOrgnummer) {
            arbeidsgiver.put("type", "Organisasjon");
            arbeidsgiver.put("organisasjonsnummer", ORGNUMMER);
        } else {
            arbeidsgiver.put("type", "Person");
            arbeidsgiver.put("offentligIdent", IDENT);
        }
        Map<String, Object> arbeidsforhold = new HashMap<>();
        arbeidsforhold.put("arbeidsgiver", arbeidsgiver);
        arbeidsforhold.put("arbeidsforholdId", "1");
        arbeidsforhold.put("arbeidstaker", arbeidstaker);
        arbeidsforhold.put("navArbeidsforholdId", NAV_ARBFORHOLD_ID);

        return newArrayList(arbeidsforhold).toArray();
    }
}