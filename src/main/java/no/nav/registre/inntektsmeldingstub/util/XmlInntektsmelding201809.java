package no.nav.registre.inntektsmeldingstub.util;

import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLArbeidsforhold;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLArbeidsgiver;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLArbeidsgiverperiodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLAvsendersystem;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLAvtaltFerieListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLDelvisFravaer;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLDelvisFravaersListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLEndringIRefusjon;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLEndringIRefusjonsListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLFravaersPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLGjenopptakelseNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLGraderingIForeldrepenger;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLGraderingIForeldrepengerListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLInntekt;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLInntektsmeldingM;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLKontaktinformasjon;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLNaturalytelseDetaljer;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLOmsorgspenger;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLOpphoerAvNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLPeriode;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLPleiepengerPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLRefusjon;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLSkjemainnhold;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLSykepengerIArbeidsgiverperioden;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLUtsettelseAvForeldrepenger;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLUtsettelseAvForeldrepengerListe;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsforhold;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsgiver;
import no.nav.registre.inntektsmeldingstub.database.model.DelvisFravaer;
import no.nav.registre.inntektsmeldingstub.database.model.GraderingIForeldrepenger;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.database.model.NaturalytelseDetaljer;
import no.nav.registre.inntektsmeldingstub.database.model.Periode;
import no.nav.registre.inntektsmeldingstub.database.model.RefusjonsEndring;
import no.nav.registre.inntektsmeldingstub.database.model.UtsettelseAvForeldrepenger;

public class XmlInntektsmelding201809 {


    private static final String NAMESPACE_URI_09 = "http://seres.no/xsd/NAV/Inntektsmelding_M/20180924";

    public static XMLInntektsmeldingM create201809Inntektsmelding(Inntektsmelding inntektsmelding) {
        if (inntektsmelding == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Må ha en inntektsmelding");
        }
        return new XMLInntektsmeldingM(new XMLSkjemainnhold(
                inntektsmelding.getYtelse(), inntektsmelding.getAarsakTilInnsending(),
                createArbeidsgiver(inntektsmelding.getArbeidsgiver()),
                inntektsmelding.getArbeidstakerFnr(), inntektsmelding.isNaerRelasjon(),
                createArbeidsforhold(inntektsmelding.getArbeidsforhold()),
                createRefusjon(inntektsmelding.getRefusjonsbeloepPrMnd(), inntektsmelding.getRefusjonsopphoersdato(), inntektsmelding.getRefusjonsEndringListe()),
                createSykepengerIArbeidsgiverperioden(inntektsmelding.getSykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt(),
                        inntektsmelding.getSykepengerBruttoUtbetalt(),
                        inntektsmelding.getSykepengerPerioder()),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "startdatoForeldrepengeperiode"), LocalDate.class, inntektsmelding.getStartdatoForeldrepengeperiode()),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "OpphoerAvNaturalytelseListe"), XMLOpphoerAvNaturalytelseListe.class, new XMLOpphoerAvNaturalytelseListe(
                        inntektsmelding.getOpphoerAvNaturalytelseListe().stream().map(XmlInntektsmelding201809::createNaturalytelse).collect(Collectors.toList())
                )),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "GjenopptakelseNaturalytelseListe"), XMLGjenopptakelseNaturalytelseListe.class, new XMLGjenopptakelseNaturalytelseListe(
                        inntektsmelding.getGjenopptakelseNaturalytelseListe().stream().map(XmlInntektsmelding201809::createNaturalytelse).collect(Collectors.toList())
                )),
                new XMLAvsendersystem(inntektsmelding.getAvsendersystemNavn(), inntektsmelding.getAvsendersystemVersjon(),
                        new JAXBElement<>(new QName(NAMESPACE_URI_09, "innsendingstidspunkt"), LocalDateTime.class, inntektsmelding.getInnsendingstidspunkt())
                ),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "PleiepengerPeriodeListe"), XMLPleiepengerPeriodeListe.class, new XMLPleiepengerPeriodeListe(
                        inntektsmelding.getPleiepengerPeriodeListe().stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList())
                )),
                createOmsorgspenger(inntektsmelding.isOmsorgHarUtbetaltPliktigeDager(), inntektsmelding.getOmsorgspengerFravaersPeriodeListe(), inntektsmelding.getOmsorgspengerDelvisFravaersListe())
        ), Collections.emptyMap());
    }

    private static JAXBElement<XMLOmsorgspenger> createOmsorgspenger(boolean omsorgHarUtbetaltPliktigeDager, List<Periode> omsorgspengerFravaersPeriodeListe, List<DelvisFravaer> omsorgspengerDelvisFravaersListe) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "Omsorgspenger"), XMLOmsorgspenger.class, new XMLOmsorgspenger(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "harUtbetaltPliktigeDager"), Boolean.class, omsorgHarUtbetaltPliktigeDager),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "fravaersPerioder"), XMLFravaersPeriodeListe.class,
                        new XMLFravaersPeriodeListe(omsorgspengerFravaersPeriodeListe.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList()))),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "delvisFravaersListe"), XMLDelvisFravaersListe.class,
                        new XMLDelvisFravaersListe(omsorgspengerDelvisFravaersListe.stream().map(XmlInntektsmelding201809::createDelvisFravaer).collect(Collectors.toList())))
        ));
    }

    private static JAXBElement<XMLSykepengerIArbeidsgiverperioden> createSykepengerIArbeidsgiverperioden(
            String sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt, double sykepengerBruttoUtbetalt, List<Periode> sykepengerPerioder) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "SykepengerIArbeidsgiverperioden"),
                XMLSykepengerIArbeidsgiverperioden.class,
                new XMLSykepengerIArbeidsgiverperioden(
                        createArbeidsgiverperiodeListe(sykepengerPerioder),
                        new JAXBElement<>(new QName(NAMESPACE_URI_09, "bruttoUtbetalt"), BigDecimal.class, new BigDecimal(sykepengerBruttoUtbetalt)),
                        new JAXBElement<>(new QName(NAMESPACE_URI_09, "begrunnelseForReduksjonEllerIkkeUtbetalt"), String.class, sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt)
                )
        );
    }

    private static JAXBElement<XMLArbeidsgiverperiodeListe> createArbeidsgiverperiodeListe(List<Periode> sykepengerPerioder) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "arbeidsgiverperiodeListe"), XMLArbeidsgiverperiodeListe.class,
                new XMLArbeidsgiverperiodeListe(
                        sykepengerPerioder.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList())
                ));
    }

    private static JAXBElement<XMLRefusjon> createRefusjon(double refusjonsbeloepPrMnd, LocalDate refusjonsopphoersdato, List<RefusjonsEndring> endringer) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "Refusjon"), XMLRefusjon.class, new XMLRefusjon(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "refusjonsbeloepPrMnd"), BigDecimal.class, new BigDecimal(refusjonsbeloepPrMnd)),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "refusjonsopphoersdato"), LocalDate.class, refusjonsopphoersdato),
                createEndringIRefusjonsListe(endringer)
        ));
    }

    private static JAXBElement<XMLEndringIRefusjonsListe> createEndringIRefusjonsListe(List<RefusjonsEndring> endringer) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "endringIRefusjonListe"), XMLEndringIRefusjonsListe.class,
                new XMLEndringIRefusjonsListe(endringer.stream().map(XmlInntektsmelding201809::createEndringIRefusjon).collect(Collectors.toList()))
        );
    }

    private static XMLArbeidsgiver createArbeidsgiver(Arbeidsgiver arbeidsgiver) {
        return new XMLArbeidsgiver(arbeidsgiver.getVirksomhetsnummer(),
                new XMLKontaktinformasjon(arbeidsgiver.getKontaktinformasjonNavn(), arbeidsgiver.getTelefonnummer())
        );
    }

    private static JAXBElement<XMLArbeidsforhold> createArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "Arbeidsforhold"), XMLArbeidsforhold.class, new XMLArbeidsforhold(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "arbeidsforholdId"), String.class, arbeidsforhold.getArbeidforholdsId()),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "foersteFravaersdag"), LocalDate.class, arbeidsforhold.getFoersteFravaersdag()),
                createInntekt(arbeidsforhold.getBeloep(), arbeidsforhold.getAarsakVedEndring()),
                createAvtaltFerieListe(arbeidsforhold.getAvtaltFerieListe()),
                createUtsettelseAvForeldrepengerListe(arbeidsforhold.getUtsettelseAvForeldrepengerListe()),
                createGraderingIForeldrepengerListe(arbeidsforhold.getGraderingIForeldrepengerListe())
        ));
    }

    private static JAXBElement<XMLGraderingIForeldrepengerListe> createGraderingIForeldrepengerListe(List<GraderingIForeldrepenger> graderingIForeldrepengerListe) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "graderingIForeldrepengerListe"),
                XMLGraderingIForeldrepengerListe.class, new XMLGraderingIForeldrepengerListe(
                graderingIForeldrepengerListe.stream().map(XmlInntektsmelding201809::createGraderingIForeldrepenger).collect(Collectors.toList())
        ));
    }

    private static JAXBElement<XMLUtsettelseAvForeldrepengerListe> createUtsettelseAvForeldrepengerListe(List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "utsettelseAvForeldrepengerListe"),
                XMLUtsettelseAvForeldrepengerListe.class, new XMLUtsettelseAvForeldrepengerListe(
                utsettelseAvForeldrepengerListe.stream().map(XmlInntektsmelding201809::createUtsettelseAvForeldrepenger).collect(Collectors.toList())
        ));
    }

    private static JAXBElement<XMLAvtaltFerieListe> createAvtaltFerieListe(List<Periode> perioder) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "avtaltFerieListe"), XMLAvtaltFerieListe.class,
                new XMLAvtaltFerieListe(perioder.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList()))
        );
    }

    private static JAXBElement<XMLInntekt> createInntekt(double beloep, String aarsekVedEndring) {
        return new JAXBElement<>(new QName(NAMESPACE_URI_09, "beregnetInntekt"), XMLInntekt.class, new XMLInntekt(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "beloep"), BigDecimal.class, new BigDecimal(beloep)),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "aarsakVedEndring"), String.class, aarsekVedEndring)
        ));
    }

    private static XMLPeriode createPeriode(Periode periode) {
        return new XMLPeriode(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "fom"), LocalDate.class, periode.getFom()),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "tom"), LocalDate.class, periode.getTom())
        );
    }

    private static XMLUtsettelseAvForeldrepenger createUtsettelseAvForeldrepenger(UtsettelseAvForeldrepenger utsettelseAvForeldrepenger) {
        return new XMLUtsettelseAvForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "periode"), XMLPeriode.class, createPeriode(utsettelseAvForeldrepenger.getPeriode())),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "aarsakTilUtsettelse"), String.class, utsettelseAvForeldrepenger.getAarsakTilUtsettelse())
        );
    }

    private static XMLGraderingIForeldrepenger createGraderingIForeldrepenger(GraderingIForeldrepenger graderingIForeldrepenger) {
        return new XMLGraderingIForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "periode"), XMLPeriode.class, createPeriode(graderingIForeldrepenger.getPeriode())),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "arbeidstidprosent"), BigInteger.class, new BigInteger(String.valueOf(graderingIForeldrepenger.getGradering())))
        );
    }

    private static XMLEndringIRefusjon createEndringIRefusjon(RefusjonsEndring endring) {
        return new XMLEndringIRefusjon(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "endringsdato"), LocalDate.class, endring.getEndringsDato()),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "refusjonsbeloepPrMnd"), BigDecimal.class, new BigDecimal(endring.getRefusjonsbeloepPrMnd()))
        );
    }

    private static XMLNaturalytelseDetaljer createNaturalytelse(NaturalytelseDetaljer naturalytelseDetaljer) {
        return new XMLNaturalytelseDetaljer(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "naturalytelseType"), String.class, naturalytelseDetaljer.getType().value()),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "fom"), LocalDate.class, naturalytelseDetaljer.getFom()),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "beloepPrMnd"), BigDecimal.class, new BigDecimal(naturalytelseDetaljer.getBeloepPrMnd()))
        );
    }

    private static XMLDelvisFravaer createDelvisFravaer(DelvisFravaer delvisFravaer) {
        return new XMLDelvisFravaer(
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "dato"), LocalDate.class, delvisFravaer.getDato()),
                new JAXBElement<>(new QName(NAMESPACE_URI_09, "timer"), BigDecimal.class, new BigDecimal(delvisFravaer.getTimer()))
        );
    }
}
