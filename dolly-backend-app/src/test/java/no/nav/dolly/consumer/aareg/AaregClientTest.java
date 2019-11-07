package no.nav.dolly.consumer.aareg;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@RunWith(MockitoJUnitRunner.class)
public class AaregClientTest {

    private static final String IDENT = "111111111111";
    private static final String ENV = "u2";
    private static final String ORGNUMMER = "222222222";
    private static final Integer NAV_ARBFORHOLD_ID = 333333333;

    @Mock
    private AaregConsumer aaregConsumer;

    @InjectMocks
    private AaregClient aaregClient;

    @Test
    public void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_OK() {
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        RsAaregResponse rsAaregResponse = RsAaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(ResponseEntity.ok(new Map[]{}));
        when(aaregConsumer.opprettArbeidsforhold(any(RsAaregOpprettRequest.class))).thenReturn(rsAaregResponse);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder().build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        verify(aaregConsumer).opprettArbeidsforhold(any(RsAaregOpprettRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_lesKasterException() {
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        RsAaregResponse rsAaregResponse = RsAaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenThrow(new RuntimeException());
        when(aaregConsumer.opprettArbeidsforhold(any(RsAaregOpprettRequest.class))).thenReturn(rsAaregResponse);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder().build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        verify(aaregConsumer).opprettArbeidsforhold(any(RsAaregOpprettRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_arbeidsgiverHarOrgnummer() {
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        RsAaregResponse rsAaregResponse = RsAaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(ResponseEntity.ok(buildArbeidsforhold(true)));
        when(aaregConsumer.oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class))).thenReturn(rsAaregResponse);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder()
                .arbeidsgiver(RsOrganisasjon.builder().orgnummer(ORGNUMMER).build())
                .arbeidstaker(RsPersonAareg.builder().ident(IDENT).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request, TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        verify(aaregConsumer).oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_arbeidsgiverHarPersonnr() {
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        RsAaregResponse rsAaregResponse = RsAaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(ResponseEntity.ok(buildArbeidsforhold(false)));
        when(aaregConsumer.oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class))).thenReturn(rsAaregResponse);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder()
                .arbeidsgiver(RsAktoerPerson.builder().ident(IDENT).build())
                .arbeidstaker(RsPersonAareg.builder().ident(IDENT).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        verify(aaregConsumer).oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_sjekkReturStatus() {

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(ResponseEntity.ok(buildArbeidsforhold(false)));
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        RsAaregResponse rsAaregResponse = RsAaregResponse.builder()
                .statusPerMiljoe(status)
                .build();
        when(aaregConsumer.oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class))).thenReturn(rsAaregResponse);

        BestillingProgress progress = new BestillingProgress();

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsArbeidsforhold.builder()
                .arbeidsgiver(RsAktoerPerson.builder().ident(IDENT).build())
                .arbeidstaker(RsPersonAareg.builder().ident(IDENT).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request, TpsPerson.builder().hovedperson(IDENT).build(), progress);

        verify(aaregConsumer).oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class));
        assertThat(progress.getAaregStatus(), is(equalTo("u2: arbforhold=1$OK")));
    }

    private Map[] buildArbeidsforhold(boolean isOrgnummer) {

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

        return new Map[]{arbeidsforhold};
    }
}