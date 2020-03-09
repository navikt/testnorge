package no.nav.registre.spion.provider.rs;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.nav.registre.spion.consumer.rs.response.HodejegerenResponse;
import no.nav.registre.spion.consumer.rs.response.aareg.AaregResponse;
import no.nav.registre.spion.consumer.rs.response.aareg.Arbeidsgiver;
import no.nav.registre.spion.consumer.rs.response.aareg.Arbeidstaker;
import no.nav.registre.spion.consumer.rs.response.aareg.Sporingsinformasjon;
import no.nav.registre.spion.domain.Vedtak;
import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;
import no.nav.registre.spion.provider.rs.response.SyntetiserSpionResponse;
import no.nav.registre.spion.provider.rs.response.SyntetiserVedtakResponse;
import no.nav.registre.spion.service.SyntetiseringService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @Mock
    private VedtakPublisher vedtakPublisher;

    @InjectMocks
    private SyntetiseringController controller;

    private static final String MILJOE = "x2";
    private static final long AVSPILLERGRUPPEID = 10;
    private HodejegerenResponse persondata;
    private AaregResponse arbeidsforhold;

    @Before
    public void setup(){
        this.persondata = new HodejegerenResponse(
                "123",
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
        arbeidsforhold = new AaregResponse(
                1234,
                "",
                new Arbeidstaker("","123",""),
                new Arbeidsgiver("organisasjon", "org_nr"),
                null,
                "",
                null,
                null,
                false,
                LocalDate.now(),
                LocalDate.now(),
                new Sporingsinformasjon()
        );
    }

    @Test
    public void genererVedtakForSPION() throws JsonProcessingException {

        List<Vedtak> vedtaksliste = Collections.singletonList(new Vedtak(
                persondata, arbeidsforhold, LocalDate.now().minusMonths(18), true ));

        List<SyntetiserVedtakResponse> res = Collections.singletonList(new SyntetiserVedtakResponse(
                "123", vedtaksliste));

        when(syntetiseringService.syntetiserVedtak(
                AVSPILLERGRUPPEID,
                MILJOE,
                1,
                null,
                null,
                1)).thenReturn(res);

        when(vedtakPublisher.publish(res)).thenReturn(1);

        SyntetiserSpionResponse faktiskResponse = controller.genererVedtakForSPION(new SyntetiserSpionRequest(
                AVSPILLERGRUPPEID, MILJOE, 1, null, null,1 ));
        SyntetiserSpionResponse forventetResponse = new SyntetiserSpionResponse(1,1);

        assertThat(faktiskResponse.getMessage()).isEqualTo(forventetResponse.getMessage());
    }

}
