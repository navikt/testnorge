package no.nav.registre.testnorge.sykemelding.provider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;

import no.nav.registre.testnorge.libs.dto.sykemelding.v1.AdresseDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.Aktivitet;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.AktivitetDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.ArbeidsgiverDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.DetaljerDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.DiagnoseDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.HelsepersonellDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.PasientDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.PeriodeDTO;
import no.nav.registre.testnorge.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.registre.testnorge.sykemelding.consumer.HendelseConsumer;
import no.nav.registre.testnorge.sykemelding.consumer.SyfoConsumer;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.sykemelding.service.SykemeldingService;

@RunWith(MockitoJUnitRunner.class)
public class SykemeldingControllerTest {

    @Mock
    private ApplicationInfo applicationInfo;

    @Mock
    private SyfoConsumer syfoConsumer;

    @Mock
    private HendelseConsumer consumer;

    @InjectMocks
    private SykemeldingService sykemeldingService;

    private SykemeldingController sykemeldingController;

    SykemeldingDTO sykemeldingRequest;
    Sykemelding sykemelding;

    @Before
    public void setUp() {
        sykemeldingController = new SykemeldingController(sykemeldingService, applicationInfo);

        when(applicationInfo.getName()).thenReturn("test");
        when(applicationInfo.getVersion()).thenReturn("1");


        sykemeldingRequest = new SykemeldingDTO(
                LocalDate.now(),
                PasientDTO.builder()
                        .ident("11111124314")
                        .fornavn("pasient")
                        .etternavn("pasientesen")
                        .adresse(AdresseDTO.builder()
                                .postnummer("9999")
                                .land("land")
                                .gate("gate")
                                .by("by")
                                .build())
                        .foedselsdato(LocalDate.now().minusYears(20))
                        .navKontor("navKontor")
                        .telefon("99999999")
                        .build(),
                HelsepersonellDTO.builder()
                        .fornavn("lege")
                        .etternavn("legesen")
                        .ident("1111118372")
                        .hprId("1234")
                        .build(),
                ArbeidsgiverDTO.builder()
                        .navn("arbeidsgiver")
                        .stillingsprosent((double) 100)
                        .yrkesbetegnelse("giver")
                        .build(),
                false,
                Collections.singletonList(PeriodeDTO.builder().aktivitet(
                        AktivitetDTO.builder()
                                .aktivitet(Aktivitet.AVVENTENDE)
                                .behandlingsdager(20)
                                .reisetilskudd(true)
                                .grad(1).build())
                        .fom(LocalDate.now().minusMonths(3))
                        .tom(LocalDate.now())
                        .build()),
                OrganisasjonDTO.builder()
                        .navn("sender")
                        .adresse(AdresseDTO.builder()
                                .by("by")
                                .gate("gate")
                                .land("land")
                                .postnummer("9999")
                                .build())
                        .orgNr("123").build(),
                OrganisasjonDTO.builder()
                        .navn("mottaker")
                        .orgNr("123")
                        .adresse(AdresseDTO.builder()
                                .by("by")
                                .gate("gate")
                                .land("land")
                                .postnummer("9999")
                                .build())
                        .build(),
                DiagnoseDTO.builder()
                        .diagnose("hovedDiagnose")
                        .diagnosekode("12345")
                        .system("system")
                        .build(),
                null,
                DetaljerDTO.builder()
                        .beskrivHensynArbeidsplassen("hensynArb")
                        .tiltakArbeidsplass("tiltakArb")
                        .arbeidsforEtterEndtPeriode(true)
                        .tiltakNav("tiltakNav")
                        .build(),
                true);

        sykemelding = new Sykemelding(sykemeldingRequest, ApplicationInfo.builder()
                .name("test")
                .version("v1")
                .build());
    }

    @Test
    public void should_send_sykemelding_to_syfo() {
        sykemeldingController.create(sykemeldingRequest);
        verify(syfoConsumer, Mockito.atMostOnce()).send(any(Sykemelding.class));
    }
}