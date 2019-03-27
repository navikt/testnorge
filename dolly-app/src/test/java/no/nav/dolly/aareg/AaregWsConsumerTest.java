package no.nav.dolly.aareg;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.BehandleArbeidsforholdPortType;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdSikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OpprettArbeidsforholdSikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.feil.Sikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsforhold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.meldinger.OppdaterArbeidsforholdRequest;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.meldinger.OpprettArbeidsforholdRequest;

@RunWith(MockitoJUnitRunner.class)
public class AaregWsConsumerTest {

    private static XMLGregorianCalendar calendar = new XMLGregorianCalendarImpl();
    private static Sikkerhetsbegrensning faultInfo = new Sikkerhetsbegrensning();

    static {
        calendar.setTime(12, 13, 0);
        calendar.setYear(2019);
        calendar.setMonth(3);
        calendar.setDay(10);
        faultInfo.setTidspunkt(calendar);
        faultInfo.setFeilaarsak("Manglende rolle");
        faultInfo.setFeilkilde("AAREG");
        faultInfo.setFeilmelding("Ingen tilgang");
    }

    @Mock
    private BehandleArbeidsforholdV1Proxy behandleArbeidsforholdV1Proxy;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private BehandleArbeidsforholdPortType behandleArbeidsforholdPortType;

    @InjectMocks
    private AaregWsConsumer aaregWsConsumer;

    @Before
    public void setuo() {

        when(behandleArbeidsforholdV1Proxy.getServiceByEnvironment(anyString())).thenReturn(behandleArbeidsforholdPortType);
    }

    @Test
    public void opprettArbeidsforhold_OK() throws Exception {

        Map<String, String> status = aaregWsConsumer.opprettArbeidsforhold(RsAaregOpprettRequest
                .builder()
                .arbeidsforhold(RsArbeidsforhold.builder().build())
                .environments(singletonList("t0"))
                .build());

        assertThat(status.get("t0"), is(equalTo("OK")));
        verify(mapperFacade).map(any(RsArbeidsforhold.class), eq(Arbeidsforhold.class));
        verify(behandleArbeidsforholdV1Proxy).getServiceByEnvironment(eq("t0"));
        verify(behandleArbeidsforholdPortType).opprettArbeidsforhold(any(OpprettArbeidsforholdRequest.class));
    }

    @Test
    public void opprettArbeidsforhold_throwSikkerhetsbegrensning() throws Exception {

        OpprettArbeidsforholdSikkerhetsbegrensning sikkerhetsbegrensning =
                new OpprettArbeidsforholdSikkerhetsbegrensning("Ingen tilgang", faultInfo);

        doThrow(sikkerhetsbegrensning).when(behandleArbeidsforholdPortType)
                .opprettArbeidsforhold(any(OpprettArbeidsforholdRequest.class));

        Map<String, String> status = aaregWsConsumer.opprettArbeidsforhold(RsAaregOpprettRequest
                .builder()
                .arbeidsforhold(RsArbeidsforhold.builder().build())
                .environments(singletonList("t0"))
                .build());

        assertThat(status.get("t0"), is(equalTo(
                "Feil, OpprettArbeidsforholdSikkerhetsbegrensning -> Ingen tilgang "
                        + "(ForretningsmessigUnntak: feilaarsak: Manglende rolle, feilkilde: AAREG, "
                        + "feilmelding: Ingen tilgang, tidspunkt: 2019-03-10T12:13)")));
    }

    @Test
    public void oppdaterArbeidsforhold_OK() throws Exception {

        RsAaregOppdaterRequest rsAaregOppdaterRequest = new RsAaregOppdaterRequest();
        rsAaregOppdaterRequest.setEnvironments(singletonList("t1"));
        rsAaregOppdaterRequest.setArbeidsforhold(RsArbeidsforhold.builder().build());
        rsAaregOppdaterRequest.setRapporteringsperiode(LocalDateTime.now());

        Map<String, String> status = aaregWsConsumer.oppdaterArbeidsforhold(rsAaregOppdaterRequest);

        assertThat(status.get("t1"), is(equalTo("OK")));
        verify(mapperFacade).map(any(RsArbeidsforhold.class), eq(Arbeidsforhold.class));
        verify(mapperFacade).map(any(LocalDateTime.class), eq(XMLGregorianCalendar.class));
        verify(behandleArbeidsforholdV1Proxy).getServiceByEnvironment(eq("t1"));
        verify(behandleArbeidsforholdPortType).oppdaterArbeidsforhold(any(OppdaterArbeidsforholdRequest.class));
    }

    @Test
    public void oppdaterArbeidsforhold_throwsSikkerhetsbegrensning() throws Exception {

        OppdaterArbeidsforholdSikkerhetsbegrensning sikkerhetsbegrensning =
                new OppdaterArbeidsforholdSikkerhetsbegrensning("Ingen tilgang", faultInfo);

        doThrow(sikkerhetsbegrensning).when(behandleArbeidsforholdPortType)
                .oppdaterArbeidsforhold(any(OppdaterArbeidsforholdRequest.class));

        RsAaregOppdaterRequest rsAaregOppdaterRequest = new RsAaregOppdaterRequest();
        rsAaregOppdaterRequest.setEnvironments(singletonList("t1"));
        rsAaregOppdaterRequest.setArbeidsforhold(RsArbeidsforhold.builder().build());
        rsAaregOppdaterRequest.setRapporteringsperiode(LocalDateTime.now());

        Map<String, String> status = aaregWsConsumer.oppdaterArbeidsforhold(rsAaregOppdaterRequest);

        assertThat(status.get("t1"), is(equalTo(
                "Feil, OppdaterArbeidsforholdSikkerhetsbegrensning -> Ingen tilgang "
                        + "(ForretningsmessigUnntak: feilaarsak: Manglende rolle, feilkilde: AAREG, "
                        + "feilmelding: Ingen tilgang, tidspunkt: 2019-03-10T12:13)")));
    }
}