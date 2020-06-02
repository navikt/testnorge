package no.nav.registre.inntektsmeldingstub.service;

import lombok.Getter;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsArbeidsforhold;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsArbeidsgiver;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsArbeidsgiverPrivat;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsAvsendersystem;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsDelvisFravaer;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsEndringIRefusjon;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsGraderingIForeldrepenger;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsInntekt;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsInntektsmelding;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsKontaktinformasjon;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsNaturaYtelseDetaljer;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsOmsorgspenger;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsPeriode;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsRefusjon;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsSykepengerIArbeidsgiverperioden;
import no.nav.registre.inntektsmeldingstub.provider.rs.RsUtsettelseAvForeldrepenger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@Getter
public class InntektsmeldingFactory {

    private static final RsArbeidsgiver fellesArbeidsgiver = RsArbeidsgiver.builder()
            .kontaktinformasjon(RsKontaktinformasjon.builder()
                    .telefonnummer("81549300")
                    .kontaktinformasjonNavn("KYKKELI KOKOS")
                    .build())
            .virksomhetsnummer("123456789")
            .build();

    private static final RsArbeidsgiverPrivat fellesPrivatArbeidsgiver = RsArbeidsgiverPrivat.builder()
            .kontaktinformasjon(RsKontaktinformasjon.builder()
                    .telefonnummer("12345678")
                    .kontaktinformasjonNavn("GUL BOLLE")
                    .build())
            .arbeidsgiverFnr("10101010101")
            .build();

    public static RsInntektsmelding getPrivatArbeidsgiverMelding() {
        return RsInntektsmelding.builder()
                .ytelse("LONN")
                .aarsakTilInnsending("AARSMELDING")
                .arbeidstakerFnr("11223344556")
                .naerRelasjon(true)
                .avsendersystem(RsAvsendersystem.builder()
                        .systemversjon("42.0.0.1")
                        .systemnavn("LONNINGSSYSTEMET")
                        .build())
                .arbeidsforhold(RsArbeidsforhold.builder()
                        .arbeidsforholdId("XSDID4213731415")
                        .beregnetInntekt(RsInntekt.builder()
                                .beloep(450200.48)
                                .build())
                        .build())
                .arbeidsgiverPrivat(fellesPrivatArbeidsgiver)
                .build();
    }

    public static RsInntektsmelding getMinimalMelding() {
        return RsInntektsmelding.builder()
                .ytelse("LONN")
                .aarsakTilInnsending("AARSMELDING")
                .arbeidstakerFnr("11223344556")
                .naerRelasjon(true)
                .avsendersystem(RsAvsendersystem.builder()
                        .systemversjon("42.0.0.1")
                        .systemnavn("LONNINGSSYSTEMET")
                        .build())
                .arbeidsforhold(RsArbeidsforhold.builder()
                        .arbeidsforholdId("XSDID4213731415")
                        .beregnetInntekt(RsInntekt.builder()
                                .beloep(450200.48)
                                .build())
                        .build())
                .arbeidsgiver(fellesArbeidsgiver)
                .build();
    }

    public static RsInntektsmelding getMinimalMeldingMedFeilLengdeKoder() {
        return RsInntektsmelding.builder()
            .ytelse("LONN")
            .aarsakTilInnsending("AARSMELDING")
            .arbeidstakerFnr("1122334455")
            .naerRelasjon(true)
            .avsendersystem(RsAvsendersystem.builder()
                    .systemversjon("42.0.0.1")
                    .systemnavn("LONNINGSSYSTEMET")
                    .build())
            .arbeidsforhold(RsArbeidsforhold.builder()
                    .arbeidsforholdId("XSDID4213731415")
                    .beregnetInntekt(RsInntekt.builder()
                            .beloep(450200.48)
                            .build())
                    .build())
            .arbeidsgiver(RsArbeidsgiver.builder()
                    .kontaktinformasjon(RsKontaktinformasjon.builder()
                            .telefonnummer("1234567")
                            .kontaktinformasjonNavn("GUL BOLLE")
                            .build())
                    .virksomhetsnummer("12345678")
                    .build())
            .build();
    }

    public static RsInntektsmelding getMinimalMeldingMedFeilRequirements() {
        return RsInntektsmelding.builder()
                .ytelse("LONN")
                .aarsakTilInnsending("AARSMELDING")
                .arbeidstakerFnr("11223344556")
                .naerRelasjon(true)
                .avsendersystem(RsAvsendersystem.builder()
                        .systemversjon("42.0.0.1")
                        .build())
                .arbeidsforhold(RsArbeidsforhold.builder().build())
                .arbeidsgiver(RsArbeidsgiver.builder()
                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                .kontaktinformasjonNavn("GUL BOLLE")
                                .telefonnummer("12345678")
                                .build())
                        .virksomhetsnummer("123456789")
                        .build())
                .build();
    }

    public static RsInntektsmelding getFullMelding() {
        return RsInntektsmelding.builder()
                .ytelse("ALLE")
                .aarsakTilInnsending("TESTING")
                .arbeidstakerFnr("11111000001")
                .naerRelasjon(false)
                .avsendersystem(RsAvsendersystem.builder()
                        .systemnavn("testsystem")
                        .systemversjon("0.1")
                        .innsendingstidspunkt(LocalDateTime.of(1995, 5, 17, 12, 48, 31))
                        .build())
                .arbeidsforhold(RsArbeidsforhold.builder()
                        .beregnetInntekt(RsInntekt.builder().beloep(108239d).aarsakVedEndring("testaarsak").build())
                        .arbeidsforholdId("17NBS82")
                        .foersteFravaersdag(LocalDate.of(1998, 1, 29))
                        .avtaltFerieListe(Arrays.asList(
                                RsPeriode.builder()
                                        .fom(LocalDate.of(1996, 6, 10))
                                        .tom(LocalDate.of(1996, 8, 19)).build(),
                                RsPeriode.builder()
                                        .fom(LocalDate.of(1997, 6, 10))
                                        .tom(LocalDate.of(1997, 8, 19)).build(),
                                RsPeriode.builder()
                                        .fom(LocalDate.of(1999, 6, 10))
                                        .tom(LocalDate.of(1999, 8, 19)).build()))
                        .graderingIForeldrepengerListe(Arrays.asList(
                                RsGraderingIForeldrepenger.builder()
                                        .arbeidstidprosent(10)
                                        .periode(RsPeriode.builder()
                                                .fom(LocalDate.of(1997, 3, 17))
                                                .tom(LocalDate.of(1997, 6, 9)).build()).build(),
                                RsGraderingIForeldrepenger.builder()
                                        .arbeidstidprosent(10)
                                        .periode(RsPeriode.builder()
                                                .fom(LocalDate.of(1998, 4, 17))
                                                .tom(LocalDate.of(1998, 8, 19)).build()).build()))
                        .utsettelseAvForeldrepengerListe(Collections.singletonList(
                                RsUtsettelseAvForeldrepenger.builder()
                                        .aarsakTilUtsettelse("Ferie")
                                        .periode(RsPeriode.builder()
                                                .fom(LocalDate.of(1998, 8, 20))
                                                .tom(LocalDate.of(1998, 10, 19)).build()).build()))
                        .build())
                .arbeidsgiver(fellesArbeidsgiver)
                .refusjon(RsRefusjon.builder()
                        .refusjonsbeloepPrMnd(700d)
                        .refusjonsopphoersdato(LocalDate.of(1999, 9, 19))
                        .endringIRefusjonListe(Arrays.asList(
                                RsEndringIRefusjon.builder().refusjonsbeloepPrMnd(80000d)
                                        .endringsdato(LocalDate.of(1998, 9, 18)).build(),
                                RsEndringIRefusjon.builder().refusjonsbeloepPrMnd(60000d)
                                        .endringsdato(LocalDate.of(1997, 3, 19)).build()))
                        .build())
                .omsorgspenger(RsOmsorgspenger.builder()
                        .fravaersPerioder(Arrays.asList(
                                RsPeriode.builder().fom(LocalDate.of(2000, 4, 28))
                                        .tom(LocalDate.of(2000, 5, 19)).build(),
                                RsPeriode.builder().fom(LocalDate.of(2002, 9, 10))
                                        .tom(LocalDate.of(2002, 11, 18)).build(),
                                RsPeriode.builder().fom(LocalDate.of(2009, 12, 8))
                                        .tom(LocalDate.of(2010, 4, 4)).build()))
                        .delvisFravaersListe(Arrays.asList(
                                RsDelvisFravaer.builder().dato(LocalDate.of(2001, 9, 10)).timer(1.4).build(),
                                RsDelvisFravaer.builder().dato(LocalDate.of(2011, 11, 9)).timer(5.9).build()))
                        .harUtbetaltPliktigeDager(true).build())
                .sykepengerIArbeidsgiverperioden(RsSykepengerIArbeidsgiverperioden.builder()
                        .arbeidsgiverperiodeListe(Arrays.asList(
                                RsPeriode.builder().fom(LocalDate.of(1996, 2, 10)).tom(LocalDate.of(1996, 5, 13)).build(),
                                RsPeriode.builder().fom(LocalDate.of(1998, 4, 8)).tom(LocalDate.of(1998, 7, 2)).build()))
                        .begrunnelseForReduksjonEllerIkkeUtbetalt("Redusert pga noe")
                        .bruttoUtbetalt(894342d).build())
                .startdatoForeldrepengeperiode(LocalDate.of(1997, 3, 17))
                .opphoerAvNaturalytelseListe(Arrays.asList(
                        RsNaturaYtelseDetaljer.builder().beloepPrMnd(432d).fom(LocalDate.of(1998, 9, 1))
                                .naturaytelseType("Treningstilbud").build(),
                        RsNaturaYtelseDetaljer.builder().beloepPrMnd(85d).fom(LocalDate.of(1998, 9, 1))
                                .naturaytelseType("Gunstig lån").build()))
                .gjenopptakelseNaturalytelseListe(Arrays.asList(
                        RsNaturaYtelseDetaljer.builder().beloepPrMnd(800d).fom(LocalDate.of(1996, 9, 1))
                                .naturaytelseType("Treningstilbud").build(),
                        RsNaturaYtelseDetaljer.builder().beloepPrMnd(632d).fom(LocalDate.of(1996, 9, 1))
                                .naturaytelseType("Gunstig lån").build()))
                .pleiepengerPerioder(Arrays.asList(
                        RsPeriode.builder().fom(LocalDate.of(1997, 3, 5)).tom(LocalDate.of(1997, 3, 30)).build(),
                        RsPeriode.builder().fom(LocalDate.of(1998, 2, 17)).tom(LocalDate.of(1998, 3, 4)).build()))
                .build();
    }

}
