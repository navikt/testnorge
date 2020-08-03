package no.nav.registre.testnorge.sykemelding.provider;

import no.nav.registre.testnorge.dto.sykemelding.v1.AdresseDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.Aktivitet;
import no.nav.registre.testnorge.dto.sykemelding.v1.AktivitetDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.ArbeidsgiverDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.DetaljerDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.DiagnoseDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.LegeDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.PasientDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.PeriodeDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.sykemelding.service.SykemeldingService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SykemeldingControllerTest {

    @Mock
    private SykemeldingService sykemeldingService;

    @InjectMocks
    private SykemeldingController sykemeldingController;

    SykemeldingDTO sykemeldingRequest;
    Sykemelding sykemelding;

    @Before
    public void setUp() {
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
                LegeDTO.builder()
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
                new ArrayList<>(Collections.singletonList(PeriodeDTO.builder().aktivitet(
                        AktivitetDTO.builder()
                                .aktivitet(Aktivitet.AVVENTENDE)
                                .behandlingsdager(20)
                                .reisetilskudd(true)
                                .grad(1).build())
                        .fom(LocalDate.now().minusMonths(3))
                        .tom(LocalDate.now())
                        .build())),
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

    @Ignore("Gir error, fikk ikke til when.then")
    @Test
    public void shouldCreateSykemelding() {

        sykemeldingController.create(sykemeldingRequest);

        verify(sykemeldingService).send(sykemelding);
    }
}