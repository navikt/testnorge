package no.nav.registre.testnorge.sykemelding.mapper;

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
import no.nav.testnav.libs.dto.sykemelding.v1.KontaktMedPasientDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PasientDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PeriodeDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.UtdypendeOpplysningerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static no.nav.testnav.libs.dto.sykemelding.v1.UtdypendeOpplysningerDTO.Restriksjon.SKJERMET_FOR_ARBEIDSGIVER;
import static no.nav.testnav.libs.dto.sykemelding.v1.UtdypendeOpplysningerDTO.Restriksjon.SKJERMET_FOR_PASIENT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SykemeldingValidateMappingStrategyTest {

    private static final String DUMMY_FNR = "12508407724";
    private final ApplicationInfo applicationInfo = ApplicationInfo.builder()
            .name("Test")
            .version("1.0.0")
            .build();
    private MapperFacade mapperFacade;

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
                        .system("123")
                        .build())
                .biDiagnoser(List.of(DiagnoseDTO.builder()
                        .diagnose("Diagnostisk prosedyre bla bla")
                        .diagnosekode("H14")
                        .system("456")
                        .build()))
                .helsepersonell(HelsepersonellDTO.builder()
                        .etternavn("September")
                        .fornavn("August")
                        .hprId("9144897")
                        .ident("20486612345")
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
                        .andreTiltak("Andre tiltak")
                        .beskrivHensynArbeidsplassen("Beskrivende hensyn til arbeidsplassen")
                        .arbeidsforEtterEndtPeriode(true)
                        .build())
                .utdypendeOpplysninger(List.of(UtdypendeOpplysningerDTO.builder()
                        .spmGruppeId("6.3")
                        .spmGruppeTekst("Gruppe 6.3")
                        .spmSvar(List.of(UtdypendeOpplysningerDTO.SvarType.builder()
                                        .spmId("6.3.1")
                                        .spmTekst("Beskriv kort sykehistorie, symptomer og funn i dagens situasjon")
                                        .svarTekst("word word word word")
                                        .restriksjon(SKJERMET_FOR_ARBEIDSGIVER)
                                        .build(),
                                UtdypendeOpplysningerDTO.SvarType.builder()
                                        .spmId("6.3.2")
                                        .spmTekst("Beskriv kort sykehistorie, symptomer og funn i dagens situasjon")
                                        .svarTekst("word word word word")
                                        .restriksjon(SKJERMET_FOR_PASIENT)
                                        .build()))
                        .build()))
                .kontaktMedPasient(KontaktMedPasientDTO.builder()
                        .kontaktDato(LocalDate.of(2024, 11, 28))
                        .begrunnelseIkkeKontakt("Begrunnelse ikke kontakt")
                        .build())
                .build();
    }

    @BeforeEach
    void setup() {

        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new SykemeldingValidateMappingStrategy());
    }

    @Test
    void validateAllFields_OK() {

        var sykemeldingDTO = getSykemeldingOK();
        var sykemelding = new Sykemelding(sykemeldingDTO, applicationInfo);
        var target = mapperFacade.map(sykemelding, ReceivedSykemeldingDTO.class);

        assertThat(target.getMsgId(), is(equalTo(sykemelding.getMsgId())));

        assertThat(target.getSykmelding().getMedisinskVurdering(), allOf(
                hasProperty("hovedDiagnose", allOf(
                        hasProperty("system", is(equalTo("123"))),
                        hasProperty("kode", is(equalTo("F43"))),
                        hasProperty("tekst", is(equalTo("Diagnostisk prosedyre IKA"))))),
                hasProperty("biDiagnoser", contains(allOf(
                        hasProperty("system", is(equalTo("456"))),
                        hasProperty("kode", is(equalTo("H14"))),
                        hasProperty("tekst", is(equalTo("Diagnostisk prosedyre bla bla")))))),
                hasProperty("yrkesskadeDato", is(equalTo(LocalDate.of(2024, 11, 21)))),
                hasProperty("svangerskap", is(equalTo(false))),
                hasProperty("yrkesskade", is(equalTo(false))),
                hasProperty("annenFraversArsak",
                        hasProperty("beskrivelse", is(equalTo("Medisinsk årsak i kategorien annet"))))));

        assertThat(target.getSykmelding().getArbeidsgiver(), allOf(
                hasProperty("harArbeidsgiver", is(equalTo(ReceivedSykemeldingDTO.ArbeidsgiverType.EN_ARBEIDSGIVER))),
                hasProperty("navn", is(equalTo(sykemeldingDTO.getArbeidsgiver().getNavn()))),
                hasProperty("yrkesbetegnelse", is(equalTo(sykemeldingDTO.getArbeidsgiver().getYrkesbetegnelse()))),
                hasProperty("stillingsprosent", is(equalTo(sykemeldingDTO.getArbeidsgiver().getStillingsprosent().intValue())))));

        assertThat(target.getSykmelding().getBehandler(), allOf(
                hasProperty("fornavn", is(equalTo(sykemeldingDTO.getHelsepersonell().getFornavn()))),
                hasProperty("mellomnavn", is(equalTo(sykemeldingDTO.getHelsepersonell().getMellomnavn()))),
                hasProperty("etternavn", is(equalTo(sykemeldingDTO.getHelsepersonell().getEtternavn()))),
                hasProperty("fnr", is(equalTo(sykemeldingDTO.getHelsepersonell().getIdent())))));

        assertThat(target.getSykmelding().getAvsenderSystem(), allOf(
                hasProperty("navn", is(equalTo(applicationInfo.getName()))),
                hasProperty("versjon", is(equalTo(applicationInfo.getVersion())))));

        assertThat(target.getPersonNrPasient(), is(equalTo(DUMMY_FNR)));
        assertThat(target.getMottattDato(), is(equalTo(sykemeldingDTO.getStartDato().atStartOfDay())));

        assertThat(target.getSykmelding().getNavnFastlege(),
                is(equalTo(sykemeldingDTO.getHelsepersonell().getFornavn() + " " + sykemeldingDTO.getHelsepersonell().getEtternavn())));
        assertThat(target.getLegekontorOrgNr(), is(equalTo(sykemeldingDTO.getMottaker().getOrgNr())));

        assertThat(target.getSykmelding().getUtdypendeOpplysninger(),
                hasEntry(is("6.3"), allOf(
                        hasEntry(is("6.3.1"), allOf(
                                hasProperty("sporsmal", is(equalTo("Beskriv kort sykehistorie, symptomer og funn i dagens situasjon"))),
                                hasProperty("svar", is(equalTo("word word word word"))),
                                hasProperty("restriksjoner", is(equalTo(List.of(SKJERMET_FOR_ARBEIDSGIVER)))))),
                        hasEntry(is("6.3.2"), allOf(
                                hasProperty("sporsmal", is(equalTo("Beskriv kort sykehistorie, symptomer og funn i dagens situasjon"))),
                                hasProperty("svar", is(equalTo("word word word word"))),
                                hasProperty("restriksjoner", is(equalTo(List.of(SKJERMET_FOR_PASIENT)))))))));

        assertThat(target.getSykmelding().getPerioder(), contains(allOf(
                hasProperty("fom", is(equalTo(LocalDate.of(2024, 11, 21)))),
                hasProperty("tom", is(equalTo(LocalDate.of(2024, 11, 27)))),
                hasProperty("gradert",
                        hasProperty("reisetilskudd", is(equalTo(false)))),
                hasProperty("behandlingsdager", is(nullValue())))));

        assertThat(target.getSykmelding().getPrognose(), allOf(
                hasProperty("arbeidsforEtterPeriode", is(equalTo(true))),
                hasProperty("hensynArbeidsplassen", is(equalTo("Beskrivende hensyn til arbeidsplassen"))),
                hasProperty("erIArbeid", allOf(
                        hasProperty("egetArbeidPaSikt", is(equalTo(true))),
                        hasProperty("annetArbeidPaSikt", is(equalTo(true))),
                        hasProperty("arbeidFOM", is(equalTo(LocalDate.of(2024, 11, 21)))),
                        hasProperty("vurderingsdato", is(equalTo(LocalDate.of(2024, 11, 21)))))),
                hasProperty("erIkkeIArbeid", is(nullValue()))));

        assertThat(target.getSykmelding().getTiltakNAV(), is(equalTo("Beskrivende tiltak fra NAV")));
        assertThat(target.getSykmelding().getTiltakArbeidsplassen(), is(equalTo("Beskrivende tiltak fra arbeidsplassen")));
        assertThat(target.getSykmelding().getAndreTiltak(), is(equalTo("Andre tiltak")));

        assertThat(target.getPersonNrLege(), is(equalTo(sykemeldingDTO.getHelsepersonell().getIdent())));

        assertThat(target.getSykmelding().getKontaktMedPasient(), allOf(
                hasProperty("kontaktDato", is(equalTo(LocalDate.of(2024, 11, 28)))),
                hasProperty("begrunnelseIkkeKontakt", is(equalTo("Begrunnelse ikke kontakt")))));
    }

    @Test
    void validateNoArbeidsgiver_OK() {

        var sykemeldingDTO = getSykemeldingOK();
        sykemeldingDTO.setArbeidsgiver(null);

        var sykemelding = new Sykemelding(sykemeldingDTO, applicationInfo);
        var target = mapperFacade.map(sykemelding, ReceivedSykemeldingDTO.class);

        assertThat(target.getSykmelding().getArbeidsgiver(), allOf(
                hasProperty("harArbeidsgiver", is(equalTo(ReceivedSykemeldingDTO.ArbeidsgiverType.INGEN_ARBEIDSGIVER)))));
    }

    @Test
    void validateNoPerioder_Failure() {

        var sykemeldingDTO = getSykemeldingOK();
        sykemeldingDTO.setPerioder(null);

        var exception = assertThrows(ResponseStatusException.class, () -> new Sykemelding(sykemeldingDTO, applicationInfo));
        assertThat(exception.getMessage(), is(equalTo("400 BAD_REQUEST \"Perioder må angis\"")));
    }

    @Test
    void validateNoKontaktDato_() {

        var sykemeldingDTO = getSykemeldingOK();
        sykemeldingDTO.getKontaktMedPasient().setKontaktDato(null);

        var sykemelding = new Sykemelding(sykemeldingDTO, applicationInfo);
        var target = mapperFacade.map(sykemelding, ReceivedSykemeldingDTO.class);

        assertThat(target.getSykmelding().getKontaktMedPasient(), allOf(
                hasProperty("kontaktDato", is(nullValue()))));
    }
}