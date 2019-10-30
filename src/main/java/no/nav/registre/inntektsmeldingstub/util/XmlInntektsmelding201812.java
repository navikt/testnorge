package no.nav.registre.inntektsmeldingstub.util;

import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsforhold;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsgiver;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsgiverPrivat;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsgiverperiodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLAvsendersystem;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLAvtaltFerieListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLDelvisFravaer;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLDelvisFravaersListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLEndringIRefusjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLEndringIRefusjonsListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLFravaersPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLGjenopptakelseNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLGraderingIForeldrepenger;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLGraderingIForeldrepengerListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntekt;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLKontaktinformasjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLNaturalytelseDetaljer;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLOmsorgspenger;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLOpphoerAvNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLPeriode;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLPleiepengerPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLRefusjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLSkjemainnhold;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLSykepengerIArbeidsgiverperioden;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLUtsettelseAvForeldrepenger;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLUtsettelseAvForeldrepengerListe;

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

public class XmlInntektsmelding201812 {

    private static final String NAMESPACE_URI = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211";

    public static XMLInntektsmeldingM createInntektsmelding(Inntektsmelding inntektsmelding) {
        return new XMLInntektsmeldingM(new XMLSkjemainnhold(
                inntektsmelding.getYtelse(),
                inntektsmelding.getAarsakTilInnsending(),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiver"), XMLArbeidsgiver.class, createArbeidsgiver(inntektsmelding.getArbeidsgiver())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiverPrivat"), XMLArbeidsgiverPrivat.class, createPrivatArbeidsgiver(inntektsmelding.getPrivatArbeidsgiver())),
                inntektsmelding.getArbeidstakerFnr(),
                inntektsmelding.isNaerRelasjon(),
                createArbeidsforhold(inntektsmelding.getArbeidsforhold()),
                createRefusjon(inntektsmelding.getRefusjonsbeloepPrMnd(),
                        inntektsmelding.getRefusjonsopphoersdato(),
                        inntektsmelding.getRefusjonsEndringListe()
                ),
                createSykepengerIArbeidsgiverperioden(inntektsmelding.getSykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt(),
                        inntektsmelding.getSykepengerBruttoUtbetalt(),
                        inntektsmelding.getSykepengerPerioder()
                ),
                new JAXBElement<>(new QName(NAMESPACE_URI, "startdatoForeldrepengeperiode"), LocalDate.class, inntektsmelding.getStartdatoForeldrepengeperiode()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "OpphoerAvNaturalytelseListe"), XMLOpphoerAvNaturalytelseListe.class, new XMLOpphoerAvNaturalytelseListe(
                        inntektsmelding.getOpphoerAvNaturalytelseListe().stream().map(XmlInntektsmelding201812::createNaturalytelse).collect(Collectors.toList())
                )),
                new JAXBElement<>(new QName(NAMESPACE_URI, "GjenopptakelseNaturalytelseListe"), XMLGjenopptakelseNaturalytelseListe.class, new XMLGjenopptakelseNaturalytelseListe(
                        inntektsmelding.getGjenopptakelseNaturalytelseListe().stream().map(XmlInntektsmelding201812::createNaturalytelse).collect(Collectors.toList())
                )),
                new XMLAvsendersystem(inntektsmelding.getAvsendersystemNavn(), inntektsmelding.getAvsendersystemVersjon(),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "innsendingstidspunkt"), LocalDateTime.class, inntektsmelding.getInnsendingstidspunkt())
                ),
                new JAXBElement<>(new QName(NAMESPACE_URI, "PleiepengerPeriodeListe"), XMLPleiepengerPeriodeListe.class, new XMLPleiepengerPeriodeListe(
                        inntektsmelding.getPleiepengerPeriodeListe().stream().map(XmlInntektsmelding201812::createPeriode).collect(Collectors.toList())
                )),
                createOmsorgspenger(inntektsmelding.isOmsorgHarUtbetaltPliktigeDager(), inntektsmelding.getOmsorgspengerFravaersPeriodeListe(), inntektsmelding.getOmsorgspengerDelvisFravaersListe())
        ), Collections.emptyMap());
    }

    private static JAXBElement<XMLOmsorgspenger> createOmsorgspenger(boolean omsorgHarUtbetaltPliktigeDager, List<Periode> omsorgspengerFravaersPeriodeListe, List<DelvisFravaer> omsorgspengerDelvisFravaersListe) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "Omsorgspenger"), XMLOmsorgspenger.class, new XMLOmsorgspenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "harUtbetaltPliktigeDager"), Boolean.class, omsorgHarUtbetaltPliktigeDager),
                new JAXBElement<>(new QName(NAMESPACE_URI, "fravaersPerioder"), XMLFravaersPeriodeListe.class,
                        new XMLFravaersPeriodeListe(omsorgspengerFravaersPeriodeListe.stream().map(XmlInntektsmelding201812::createPeriode).collect(Collectors.toList()))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "delvisFravaersListe"), XMLDelvisFravaersListe.class,
                        new XMLDelvisFravaersListe(omsorgspengerDelvisFravaersListe.stream().map(XmlInntektsmelding201812::createDelvisFravaer).collect(Collectors.toList())))
        ));
    }

    private static JAXBElement<XMLSykepengerIArbeidsgiverperioden> createSykepengerIArbeidsgiverperioden(
            String sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt, double sykepengerBruttoUtbetalt, List<Periode> sykepengerPerioder) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "SykepengerIArbeidsgiverperioden"),
                XMLSykepengerIArbeidsgiverperioden.class,
                new XMLSykepengerIArbeidsgiverperioden(
                        createArbeidsgiverperiodeListe(sykepengerPerioder),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "bruttoUtbetalt"), BigDecimal.class, new BigDecimal(sykepengerBruttoUtbetalt)),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "begrunnelseForReduksjonEllerIkkeUtbetalt"), String.class, sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt)
                )
        );
    }

    private static JAXBElement<XMLArbeidsgiverperiodeListe> createArbeidsgiverperiodeListe(List<Periode> sykepengerPerioder) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiverperiodeListe"), XMLArbeidsgiverperiodeListe.class,
                new XMLArbeidsgiverperiodeListe(
                        sykepengerPerioder.stream().map(XmlInntektsmelding201812::createPeriode).collect(Collectors.toList())
                ));
    }

    private static JAXBElement<XMLRefusjon> createRefusjon(double refusjonsbeloepPrMnd, LocalDate refusjonsopphoersdato, List<RefusjonsEndring> endringer) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "Refusjon"), XMLRefusjon.class, new XMLRefusjon(
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class, new BigDecimal(refusjonsbeloepPrMnd)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsopphoersdato"), LocalDate.class, refusjonsopphoersdato),
                createEndringIRefusjonsListe(endringer)
        ));
    }

    private static JAXBElement<XMLEndringIRefusjonsListe> createEndringIRefusjonsListe(List<RefusjonsEndring> endringer) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "endringIRefusjonListe"), XMLEndringIRefusjonsListe.class,
                new XMLEndringIRefusjonsListe(endringer.stream().map(XmlInntektsmelding201812::createEndringIRefusjon).collect(Collectors.toList()))
        );
    }

    private static XMLArbeidsgiver createArbeidsgiver(Arbeidsgiver arbeidsgiver) {
        return new XMLArbeidsgiver(arbeidsgiver.getVirksomhetsnummer(),
                new XMLKontaktinformasjon(arbeidsgiver.getKontaktinformasjonNavn(), arbeidsgiver.getTelefonnummer())
        );
    }

    private static XMLArbeidsgiverPrivat createPrivatArbeidsgiver(Arbeidsgiver arbeidsgiver) {
        return new XMLArbeidsgiverPrivat(arbeidsgiver.getVirksomhetsnummer(),
                new XMLKontaktinformasjon(arbeidsgiver.getKontaktinformasjonNavn(), arbeidsgiver.getTelefonnummer()));
    }

    private static JAXBElement<XMLArbeidsforhold> createArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "Arbeidsforhold"), XMLArbeidsforhold.class, new XMLArbeidsforhold(
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforholdId"), String.class, arbeidsforhold.getArbeidforholdsId()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "foersteFravaersdag"), LocalDate.class, arbeidsforhold.getFoersteFravaersdag()),
                createInntekt(arbeidsforhold.getBeloep(), arbeidsforhold.getAarsakVedEndring()),
                createAvtaltFerieListe(arbeidsforhold.getAvtaltFerieListe()),
                createUtsettelseAvForeldrepengerListe(arbeidsforhold.getUtsettelseAvForeldrepengerListe()),
                createGraderingIForeldrepengerListe(arbeidsforhold.getGraderingIForeldrepengerListe())
        ));
    }

    private static JAXBElement<XMLGraderingIForeldrepengerListe> createGraderingIForeldrepengerListe(List<GraderingIForeldrepenger> graderingIForeldrepengerListe) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "graderingIForeldrepengerListe"),
                XMLGraderingIForeldrepengerListe.class, new XMLGraderingIForeldrepengerListe(
                graderingIForeldrepengerListe.stream().map(XmlInntektsmelding201812::createGraderingIForeldrepenger).collect(Collectors.toList())
        ));
    }

    private static JAXBElement<XMLUtsettelseAvForeldrepengerListe> createUtsettelseAvForeldrepengerListe(List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "utsettelseAvForeldrepengerListe"),
                XMLUtsettelseAvForeldrepengerListe.class, new XMLUtsettelseAvForeldrepengerListe(
                utsettelseAvForeldrepengerListe.stream().map(XmlInntektsmelding201812::createUtsettelseAvForeldrepenger).collect(Collectors.toList())
        ));
    }

    private static JAXBElement<XMLAvtaltFerieListe> createAvtaltFerieListe(List<Periode> perioder) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "avtaltFerieListe"), XMLAvtaltFerieListe.class,
                new XMLAvtaltFerieListe(perioder.stream().map(XmlInntektsmelding201812::createPeriode).collect(Collectors.toList()))
        );
    }

    private static JAXBElement<XMLInntekt> createInntekt(double beloep, String aarsekVedEndring) {
        return new JAXBElement<>(new QName(NAMESPACE_URI, "beregnetInntekt"), XMLInntekt.class, new XMLInntekt(
                new JAXBElement<>(new QName(NAMESPACE_URI, "beloep"), BigDecimal.class, new BigDecimal(beloep)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "aarsakVedEndring"), String.class, aarsekVedEndring)
        ));
    }

    private static XMLPeriode createPeriode(Periode periode) {
        return new XMLPeriode(
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, periode.getFom()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "tom"), LocalDate.class, periode.getTom())
        );
    }

    private static XMLUtsettelseAvForeldrepenger createUtsettelseAvForeldrepenger(UtsettelseAvForeldrepenger utsettelseAvForeldrepenger) {
        return new XMLUtsettelseAvForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "periode"), XMLPeriode.class, createPeriode(utsettelseAvForeldrepenger.getPeriode())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "aarsakTilUtsettelse"), String.class, utsettelseAvForeldrepenger.getAarsakTilUtsettelse())
        );
    }

    private static XMLGraderingIForeldrepenger createGraderingIForeldrepenger(GraderingIForeldrepenger graderingIForeldrepenger) {
        return new XMLGraderingIForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "periode"), XMLPeriode.class, createPeriode(graderingIForeldrepenger.getPeriode())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidstidprosent"), BigInteger.class, new BigInteger(String.valueOf(graderingIForeldrepenger.getGradering())))
        );
    }

    private static XMLEndringIRefusjon createEndringIRefusjon(RefusjonsEndring endring) {
        return new XMLEndringIRefusjon(
                new JAXBElement<>(new QName(NAMESPACE_URI, "endringsdato"), LocalDate.class, endring.getEndringsDato()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class, new BigDecimal(endring.getRefusjonsbeloepPrMnd()))
        );
    }

    private static XMLNaturalytelseDetaljer createNaturalytelse(NaturalytelseDetaljer naturalytelseDetaljer) {
        return new XMLNaturalytelseDetaljer(
                new JAXBElement<>(new QName(NAMESPACE_URI, "naturalytelseType"), String.class, naturalytelseDetaljer.getType()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, naturalytelseDetaljer.getFom()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "beloepPrMnd"), BigDecimal.class, new BigDecimal(naturalytelseDetaljer.getBeloepPrMnd()))
        );
    }

    private static XMLDelvisFravaer createDelvisFravaer(DelvisFravaer delvisFravaer) {
        return new XMLDelvisFravaer(
                new JAXBElement<>(new QName(NAMESPACE_URI, "dato"), LocalDate.class, delvisFravaer.getDato()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "timer"), BigDecimal.class, new BigDecimal(delvisFravaer.getTimer()))
        );
    }
}
