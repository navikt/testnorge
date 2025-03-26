package no.nav.dolly.bestilling.inntektsmelding.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilInnsendingType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilUtsettelseType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakVedEndringType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.BegrunnelseForReduksjonEllerIkkeUtbetaltType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.NaturalytelseType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.YtelseType;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakBeregnetInntektEndringKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


@ExtendWith(MockitoExtension.class)
class InntektsmeldingMappingStrategyTest {

    private static final LocalDate START_DATO = LocalDate.of(2020, 1, 1);
    private static final LocalDate SLUTT_DATO = LocalDate.of(2023, 12, 31);
    private static final LocalDateTime INNSENDING_TIDSPUNKT = LocalDateTime.of(2024, 2, 8, 9, 48);

    private static final String DUMMY = "dummy";

    private MapperFacade mapperFacade;

    private RsInntektsmelding populateRsInntektsmelding() {

        return RsInntektsmelding.builder()
                .joarkMetadata(RsInntektsmelding.JoarkMetadata.builder()
                        .avsenderMottakerIdType(DUMMY)
                        .brevkategori(DUMMY)
                        .brevkode(DUMMY)
                        .brukerIdType(DUMMY)
                        .eksternReferanseId(DUMMY)
                        .filtypeArkiv(DUMMY)
                        .filtypeOriginal(DUMMY)
                        .journalpostType(DUMMY)
                        .kanal(DUMMY)
                        .tema(DUMMY)
                        .tittel(DUMMY)
                        .variantformatArkiv(DUMMY)
                        .variantformatOriginal(DUMMY)
                        .build())
                .inntekter(List.of(RsInntektsmelding.Inntektsmelding.builder()
                        .aarsakTilInnsending(AarsakTilInnsendingType.NY)
                        .arbeidsforhold(RsInntektsmelding.RsArbeidsforhold.builder()
                                .arbeidsforholdId("1")
                                .avtaltFerieListe(List.of(getPeriode()))
                                .beregnetInntekt(RsInntektsmelding.RsAltinnInntekt.builder()
                                        .aarsakVedEndring(AarsakVedEndringType.TARIFFENDRING)
                                        .beloep(1.0)
                                        .build())
                                .foersteFravaersdag(START_DATO)
                                .graderingIForeldrepengerListe(List.of(RsInntektsmelding.RsGraderingIForeldrepenger.builder()
                                        .arbeidstidprosent(100)
                                        .periode(getPeriode())
                                        .build()))
                                .utsettelseAvForeldrepengerListe(List.of(RsInntektsmelding.RsUtsettelseAvForeldrepenger.builder()
                                        .aarsakTilUtsettelse(AarsakTilUtsettelseType.LOVBESTEMT_FERIE)
                                        .periode(getPeriode())
                                        .build()))
                                .build())
                        .arbeidsgiver(RsInntektsmelding.RsArbeidsgiver.builder()
                                .kontaktinformasjon(getKontaktinformasjon())
                                .virksomhetsnummer(DUMMY)
                                .build())
                        .arbeidsgiverPrivat(RsInntektsmelding.RsArbeidsgiverPrivat.builder()
                                .kontaktinformasjon(getKontaktinformasjon())
                                .arbeidsgiverFnr(DUMMY)
                                .build())
                        .avsendersystem(RsInntektsmelding.RsAvsendersystem.builder()
                                .innsendingstidspunkt(INNSENDING_TIDSPUNKT)
                                .systemnavn(DUMMY)
                                .systemversjon("1")
                                .build())
                        .gjenopptakelseNaturalytelseListe(List.of(getNaturalYtelser()))
                        .naerRelasjon(true)
                        .omsorgspenger(RsInntektsmelding.RsOmsorgspenger.builder()
                                .delvisFravaersListe(List.of(RsInntektsmelding.RsDelvisFravaer.builder()
                                        .dato(START_DATO)
                                        .timer(100.0)
                                        .build()))
                                .fravaersPerioder(List.of(getPeriode()))
                                .harUtbetaltPliktigeDager(true)
                                .build())
                        .opphoerAvNaturalytelseListe(List.of(getNaturalYtelser()))
                        .pleiepengerPerioder(List.of(getPeriode()))
                        .refusjon(RsInntektsmelding.RsRefusjon.builder()
                                .endringIRefusjonListe(List.of(RsInntektsmelding.RsEndringIRefusjon.builder()
                                        .endringsdato(START_DATO)
                                        .refusjonsbeloepPrMnd(1000.0)
                                        .build()))
                                .refusjonsbeloepPrMnd(500.0)
                                .refusjonsopphoersdato(SLUTT_DATO)
                                .build())
                        .startdatoForeldrepengeperiode(START_DATO)
                        .sykepengerIArbeidsgiverperioden(RsInntektsmelding.RsSykepengerIArbeidsgiverperioden.builder()
                                .arbeidsgiverperiodeListe(List.of(getPeriode()))
                                .bruttoUtbetalt(100.0)
                                .begrunnelseForReduksjonEllerIkkeUtbetalt(BegrunnelseForReduksjonEllerIkkeUtbetaltType.BETVILER_ARBEIDSUFOERHET)
                                .build())
                        .ytelse(YtelseType.OPPLAERINGSPENGER)
                        .build()))
                .build();
    }

    private static RsInntektsmelding.RsPeriode getPeriode() {

        return RsInntektsmelding.RsPeriode.builder()
                .fom(START_DATO)
                .tom(SLUTT_DATO)
                .build();
    }

    private static RsInntektsmelding.RsKontaktinformasjon getKontaktinformasjon() {

        return RsInntektsmelding.RsKontaktinformasjon.builder()
                .kontaktinformasjonNavn(DUMMY)
                .telefonnummer(DUMMY)
                .build();
    }

    private static RsInntektsmelding.RsNaturalYtelseDetaljer getNaturalYtelser() {

        return RsInntektsmelding.RsNaturalYtelseDetaljer.builder()
                .beloepPrMnd(1000.0)
                .naturalytelseType(NaturalytelseType.BEDRIFTSBARNEHAGEPLASS)
                .fom(START_DATO)
                .build();
    }

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new InntektsmeldingMappingStrategy());
    }

    @Test
    void mapJoarkdataAllefelter() {

        var target = mapperFacade.map(populateRsInntektsmelding(), InntektsmeldingRequest.class);
        assertThat(target.getJoarkMetadata().getJournalpostType(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getAvsenderMottakerIdType(), is(equalTo("ORGNR")));
        assertThat(target.getJoarkMetadata().getBrukerIdType(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getTema(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getTittel(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getKanal(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getEksternReferanseId(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getFiltypeOriginal(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getFiltypeArkiv(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getVariantformatOriginal(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getVariantformatArkiv(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getBrevkode(), is(equalTo(DUMMY)));
        assertThat(target.getJoarkMetadata().getBrevkategori(), is(equalTo(DUMMY)));
    }

    @Test
    void mapJoarkdataEmptyyMedIdent() {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", "123");

        var kilde = populateRsInntektsmelding();
        kilde.setJoarkMetadata(null);

        var target = mapperFacade.map(new RsInntektsmelding(), InntektsmeldingRequest.class, context);
        assertThat(target.getArbeidstakerFnr(), is(equalTo("123")));
        assertThat(target.getJoarkMetadata().getAvsenderMottakerIdType(), is(equalTo("FNR")));
    }

    @Test
    void mapInntektsmeldingSjekkeEnumFelter() {

        var original = populateRsInntektsmelding();
        var mapped = mapperFacade.map(original, InntektsmeldingRequest.class).getInntekter().getFirst();
        assertThat(mapped.getAarsakTilInnsending(), is(equalTo(AarsakInnsendingKodeListe.NY)));
        assertThat(mapped.getArbeidsforhold().getBeregnetInntekt().getAarsakVedEndring(), is(equalTo("TARIFFENDRING")));
        assertThat(mapped.getArbeidsforhold().getUtsettelseAvForeldrepengerListe().getFirst().getAarsakTilUtsettelse(), is(equalTo("LOVBESTEMT_FERIE")));
        assertThat(mapped.getSykepengerIArbeidsgiverperioden().getBegrunnelseForReduksjonEllerIkkeUtbetalt(), is(equalTo("BETVILER_ARBEIDSUFOERHET")));
        assertThat(mapped.getYtelse(), is(equalTo(YtelseKodeListe.OPPLAERINGSPENGER)));

        for (NaturalytelseType originalNaturalytelseType : NaturalytelseType.values()) {
            original.getInntekter().getFirst().getGjenopptakelseNaturalytelseListe().getFirst().setNaturalytelseType(originalNaturalytelseType);
            original.getInntekter().getFirst().getOpphoerAvNaturalytelseListe().getFirst().setNaturalytelseType(originalNaturalytelseType);
            mapped = mapperFacade.map(original, InntektsmeldingRequest.class).getInntekter().getFirst();
            var mappedNaturalytelseType = mapped
                    .getOpphoerAvNaturalytelseListe()
                    .getFirst()
                    .getNaturalytelseType();
            assertThat(mappedNaturalytelseType, is(equalTo(originalNaturalytelseType.getJsonValue())));
            originalNaturalytelseType = original
                    .getInntekter()
                    .getFirst()
                    .getGjenopptakelseNaturalytelseListe()
                    .getFirst()
                    .getNaturalytelseType();
            mappedNaturalytelseType = mapped
                    .getGjenopptakelseNaturalytelseListe()
                    .getFirst()
                    .getNaturalytelseType();
            assertThat(mappedNaturalytelseType, is(equalTo(originalNaturalytelseType.getJsonValue())));
        }
    }

    @Test
    void mapSykepengerIArbeidsgiverperioden() {

        var target = mapperFacade.map(populateRsInntektsmelding(), InntektsmeldingRequest.class)
                .getInntekter().getFirst().getSykepengerIArbeidsgiverperioden();
        assertThat(target.getArbeidsgiverperiodeListe().getFirst().getFom(),
                is(equalTo(START_DATO.toString())));
        assertThat(target.getArbeidsgiverperiodeListe().getFirst().getTom(),
                is(equalTo(SLUTT_DATO.toString())));
        assertThat(target.getBruttoUtbetalt(), is(equalTo(100.0)));
        assertThat(target.getBegrunnelseForReduksjonEllerIkkeUtbetalt(),
                is(equalTo(BegrunnelseForReduksjonEllerIkkeUtbetaltType.BETVILER_ARBEIDSUFOERHET.name())));
    }

    @Test
    void mapRefusjon() {

        var target = mapperFacade.map(populateRsInntektsmelding(), InntektsmeldingRequest.class)
                .getInntekter().getFirst().getRefusjon();
        assertThat(target.getEndringIRefusjonListe().getFirst().getEndringsdato(), is(equalTo(START_DATO.toString())));
        assertThat(target.getEndringIRefusjonListe().getFirst().getRefusjonsbeloepPrMnd(), is(equalTo(1000.0)));
        assertThat(target.getRefusjonsbeloepPrMnd(), is(equalTo(500.0)));
        assertThat(target.getRefusjonsopphoersdato(), is(equalTo(SLUTT_DATO.toString())));
    }

    @Test
    void mapOmsorgspenger() {

        var target = mapperFacade.map(populateRsInntektsmelding(), InntektsmeldingRequest.class)
                .getInntekter().getFirst().getOmsorgspenger();
        assertThat(target.getFravaersPerioder().getFirst().getFom(), is(equalTo(START_DATO.toString())));
        assertThat(target.getFravaersPerioder().getFirst().getTom(), is(equalTo(SLUTT_DATO.toString())));
        assertThat(target.getDelvisFravaersListe().getFirst().getDato(), is(equalTo(START_DATO.toString())));
        assertThat(target.getDelvisFravaersListe().getFirst().getTimer(), is(equalTo(100.0)));
        assertThat(target.getHarUtbetaltPliktigeDager(), is(true));
    }

    @Test
    void mapArbeidsforhold() {
        var target = mapperFacade.map(populateRsInntektsmelding(), InntektsmeldingRequest.class)
                .getInntekter().getFirst().getArbeidsforhold();

        assertThat(target.getArbeidsforholdId(), is(equalTo("1")));
        assertThat(target.getAvtaltFerieListe().getFirst().getFom(), is(equalTo(START_DATO.toString())));
        assertThat(target.getAvtaltFerieListe().getFirst().getTom(), is(equalTo(SLUTT_DATO.toString())));
        assertThat(target.getBeregnetInntekt().getAarsakVedEndring(),
                is(equalTo(AarsakBeregnetInntektEndringKodeListe.TARIFFENDRING.name())));
        assertThat(target.getBeregnetInntekt().getBeloep(), is(equalTo(1.0)));
        assertThat(target.getFoersteFravaersdag(), is(equalTo(START_DATO.toString())));
        assertThat(target.getGraderingIForeldrepengerListe().getFirst().getArbeidstidprosent(), is(equalTo(100)));
        assertThat(target.getGraderingIForeldrepengerListe().getFirst().getPeriode().getFom(), is(equalTo(START_DATO.toString())));
        assertThat(target.getGraderingIForeldrepengerListe().getFirst().getPeriode().getTom(), is(equalTo(SLUTT_DATO.toString())));
        assertThat(target.getUtsettelseAvForeldrepengerListe().getFirst().getAarsakTilUtsettelse(),
                is(equalTo(AarsakTilUtsettelseType.LOVBESTEMT_FERIE.name())));
        assertThat(target.getUtsettelseAvForeldrepengerListe().getFirst().getPeriode().getFom(), is(equalTo(START_DATO.toString())));
        assertThat(target.getUtsettelseAvForeldrepengerListe().getFirst().getPeriode().getTom(), is(equalTo(SLUTT_DATO.toString())));
    }
}
