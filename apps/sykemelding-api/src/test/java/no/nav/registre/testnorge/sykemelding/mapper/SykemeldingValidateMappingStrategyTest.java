package no.nav.registre.testnorge.sykemelding.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.sykemelding.dto.ReceivedSykemeldingDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.AdresseDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.AktivitetDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.ArbeidsgiverDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.DetaljerDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.DiagnoseDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.HelsepersonellDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PasientDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PeriodeDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class SykemeldingValidateMappingStrategyTest {

    private static final String DUMMY_FNR = "12508407724";

    private MapperFacade mapperFacade;

    private ApplicationInfo applicationInfo = ApplicationInfo.builder()
            .name("Test")
            .version("1.0.0")
            .build();

    @BeforeEach
    void setup() {

        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new SykemeldingValidateMappingStrategy(new ObjectMapper()));
    }

    @Test
    void validate() {

        var sykemeldingDTO = getSykemeldingOK();
        var sykemelding = new Sykemelding(sykemeldingDTO, applicationInfo);
        var target = mapperFacade.map(sykemelding, ReceivedSykemeldingDTO.class);

        assertThat(target.getMsgId(), is(equalTo(sykemelding.getMsgId())));
        assertThat(target.getSykmelding().getMedisinskVurdering().getYrkesskadeDato(), is(equalTo(LocalDate.of(2024, 11, 21))));
        assertThat(target.getSykmelding().getMedisinskVurdering().getSvangerskap(), is(equalTo(false)));
        assertThat(target.getSykmelding().getMedisinskVurdering().getYrkesskade(), is(equalTo(false)));
        assertThat(target.getSykmelding().getMedisinskVurdering().getAnnenFraversArsak().getBeskrivelse(), is(equalTo("Medising årsak i kategorien annet")));

        assertThat(target.getSykmelding().getArbeidsgiver().getHarArbeidsgiver(), is(equalTo(ReceivedSykemeldingDTO.ArbeidsgiverType.EN_ARBEIDSGIVER)));
        assertThat(target.getSykmelding().getArbeidsgiver().getStillingsprosent(), is(equalTo(sykemeldingDTO.getArbeidsgiver().getStillingsprosent().intValue())));
        assertThat(target.getSykmelding().getArbeidsgiver().getYrkesbetegnelse(), is(equalTo(sykemeldingDTO.getArbeidsgiver().getYrkesbetegnelse())));
        assertThat(target.getSykmelding().getArbeidsgiver().getNavn(), is(equalTo(sykemeldingDTO.getArbeidsgiver().getNavn())));

        assertThat(target.getSykmelding().getBehandler().getFornavn(), is(equalTo(sykemeldingDTO.getHelsepersonell().getFornavn())));
        assertThat(target.getSykmelding().getBehandler().getMellomnavn(), is(equalTo(sykemeldingDTO.getHelsepersonell().getMellomnavn())));
        assertThat(target.getSykmelding().getBehandler().getEtternavn(), is(equalTo(sykemeldingDTO.getHelsepersonell().getEtternavn())));
        assertThat(target.getSykmelding().getBehandler().getFnr(), is(equalTo(sykemeldingDTO.getHelsepersonell().getIdent())));

        assertThat(target.getSykmelding().getAvsenderSystem().getNavn(), is(equalTo(applicationInfo.getName())));
        assertThat(target.getSykmelding().getAvsenderSystem().getVersjon(), is(equalTo(applicationInfo.getVersion())));

        assertThat(target.getPersonNrPasient(), is(equalTo(DUMMY_FNR)));
        assertThat(target.getMottattDato(), is(equalTo(sykemeldingDTO.getStartDato().atStartOfDay())));
//        assertThat(target.getSykmelding().getPerioder(), hasItems(ReceivedSykemeldingDTO.Periode.builder()
//                .fom(sykemelding.getFom())
//                .tom(sykemelding.getTom())
//                .build()));
        assertThat(target.getSykmelding().getNavnFastlege(),
                is(equalTo(sykemeldingDTO.getHelsepersonell().getFornavn() + " " + sykemeldingDTO.getHelsepersonell().getEtternavn())));
        assertThat(target.getLegekontorOrgNr(), is(equalTo(sykemeldingDTO.getMottaker().getOrgNr())));
    }

    private SykemeldingDTO getSykemeldingOK() {

        return SykemeldingDTO.builder()
                .arbeidsgiver(ArbeidsgiverDTO.builder()
                        .stillingsprosent(100.0F)
                        .yrkesbetegnelse("8141108")
                        .navn("Herbert Gran")
                        .build())
                .detaljer(DetaljerDTO.builder()
                        .arbeidsforEtterEndtPeriode(false)
                        .build())
                .hovedDiagnose(DiagnoseDTO.builder()
                        .diagnose("Diagnostisk prosedyre IKA")
                        .diagnosekode("F43")
                        .system("2.16.578.1.12.4.1.1.7170")
                        .build())
                .helsepersonell(HelsepersonellDTO.builder()
                        .etternavn("September")
                        .fornavn("August")
                        .hprId("9144897")
                        .ident("20086600138")
                        .samhandlerType("LEGE")
                        .build())
                .manglendeTilretteleggingPaaArbeidsplassen(false)
                .mottaker(OrganisasjonDTO.builder()
                        .adresse(AdresseDTO.builder().build())
                        .build())
                .pasient(PasientDTO.builder()
                        .adresse(AdresseDTO.builder()
                                .by("HONNINGSVÅG")
                                .gate("Hjellplassen 1")
                                .land("NOR")
                                .postnummer("9750")
                                .build())
                        .etternavn("SUM")
                        .foedselsdato(LocalDate.of(1984, 10, 12))
                        .fornavn("TAPPER")
                        .ident("12508407724")
                        .navKontor("2019")
                        .build())
                .perioder(List.of(PeriodeDTO.builder()
                        .aktivitet(AktivitetDTO.builder()
                                .behandlingsdager(0)
                                .grad(0)
                                .reisetilskudd(false)
                                .build())
                        .fom(LocalDate.of(2024, 11, 21))
                        .tom(LocalDate.of(2024, 11, 27))
                        .build()))
                .sender(OrganisasjonDTO.builder()
                        .adresse(AdresseDTO.builder()
                                .by("Oslo")
                                .gate("Sannergata 2")
                                .land("NOR")
                                .postnummer("0557")
                                .build())
                        .build())
                .startDato(LocalDate.of(2024, 11, 28))
                .umiddelbarBistand(false)
                .detaljer(DetaljerDTO.builder()
                        .tiltakArbeidsplass("Beskrivende tiltak fra arbeidsplassen")
                        .tiltakNav("Beskrivende tiltak fra NAV")
                        .beskrivHensynArbeidsplassen("Beskrivende hensyn til arbeidsplassen")
                        .arbeidsforEtterEndtPeriode(true)
                        .build())
                .build();
    }
}