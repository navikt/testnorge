package no.nav.registre.aareg.service;

import static no.nav.registre.aareg.consumer.ws.AaregWsConsumer.STATUS_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import no.nav.registre.aareg.consumer.rs.AaregRestConsumer;
import no.nav.registre.aareg.consumer.ws.AaregWsConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Ansettelsesperiode;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsavtale;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Organisasjon;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Periode;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Person;

@ExtendWith(MockitoExtension.class)
class AaregServiceTest {

    @Mock
    private AaregWsConsumer aaregWsConsumer;

    @Mock
    private AaregRestConsumer aaregRestConsumer;

    @InjectMocks
    private AaregService aaregService;

    private RsAaregOpprettRequest rsAaregOpprettRequest;
    private RsAaregOppdaterRequest rsAaregOppdaterRequest;
    private String ident = "01010101010";
    private String miljoe = "t1";
    private String navCallId = "test";

    @BeforeEach
    public void setUp() {
        rsAaregOpprettRequest = new RsAaregOpprettRequest();
        rsAaregOppdaterRequest = new RsAaregOppdaterRequest();
    }

    @Test
    void shouldOppretteArbeidsforhold() {
        aaregService.opprettArbeidsforhold(rsAaregOpprettRequest);
        verify(aaregWsConsumer).opprettArbeidsforhold(rsAaregOpprettRequest);
    }

    @Test
    void shouldOppdatereArbeidsforhold() {
        aaregService.oppdaterArbeidsforhold(rsAaregOppdaterRequest);
        verify(aaregWsConsumer).oppdaterArbeidsforhold(rsAaregOppdaterRequest);
    }

    @Test
    void shouldHenteArbeidsforhold() {
        aaregService.hentArbeidsforhold(ident, miljoe);
        verify(aaregRestConsumer).hentArbeidsforhold(ident, miljoe);
    }

    @Test
    void shouldSletteArbeidsforhold() {
        var arbeidsforhold = buildArbeidsforhold();
        Map<String, String> status = new HashMap<>();
        status.put(miljoe, STATUS_OK);
        when(aaregRestConsumer.hentArbeidsforhold(ident, miljoe)).thenReturn(ResponseEntity.ok(Collections.singletonList(arbeidsforhold)));
        when(aaregWsConsumer.oppdaterArbeidsforhold(any())).thenReturn(status);

        var result = aaregService.slettArbeidsforhold(ident, Collections.singletonList(miljoe), navCallId);

        assertThat(result.getStatusPerMiljoe().get(miljoe), equalTo(STATUS_OK));

        verify(aaregRestConsumer).hentArbeidsforhold(ident, miljoe);
        verify(aaregWsConsumer).oppdaterArbeidsforhold(any());
    }

    private Arbeidsforhold buildArbeidsforhold() {
        return Arbeidsforhold.builder()
                .arbeidsforholdId("y6LJXvtsU57l2sTU")
                .navArbeidsforholdId(3053173L)
                .arbeidstaker(Person.builder()
                        .offentligIdent("16018809048")
                        .aktoerId("1675247299346")
                        .build())
                .arbeidsgiver(Organisasjon.builder()
                        .organisasjonsnummer("874623512")
                        .build())
                .type("ordinaertArbeidsforhold")
                .opplysningspliktig(Organisasjon.builder()
                        .organisasjonsnummer("970490361")
                        .build())
                .ansettelsesperiode(Ansettelsesperiode.builder()
                        .periode(Periode.builder()
                                .fom(LocalDate.of(2012, 10, 14))
                                .build())
                        .build())
                .arbeidsavtaler(Collections.singletonList(Arbeidsavtale.builder()
                        .yrke("8279102")
                        .build()))
                .build();
    }
}