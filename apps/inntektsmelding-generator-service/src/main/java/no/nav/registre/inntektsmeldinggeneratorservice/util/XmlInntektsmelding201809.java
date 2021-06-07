package no.nav.registre.inntektsmeldinggeneratorservice.util;

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
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsforhold;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiver;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsAvsendersystem;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsDelvisFravaer;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsEndringIRefusjon;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsGraderingIForeldrepenger;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntekt;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsNaturalytelseDetaljer;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsOmsorgspenger;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsPeriode;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsRefusjon;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsSykepengerIArbeidsgiverperioden;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsUtsettelseAvForeldrepenger;

public class XmlInntektsmelding201809 {

    private XmlInntektsmelding201809() {
    }

    private static final String NAMESPACE_URI = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211";

    public static XMLInntektsmeldingM createInntektsmelding(RsInntektsmelding melding) {
        return new XMLInntektsmeldingM(new XMLSkjemainnhold(
                melding.getYtelse(),
                melding.getAarsakTilInnsending(),
                createArbeidsgiver(melding.getArbeidsgiver().orElse(null)),
                melding.getArbeidstakerFnr(),
                melding.isNaerRelasjon(),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforhold"),
                        XMLArbeidsforhold.class,
                        createArbeidsforhold(melding.getArbeidsforhold())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjon"),
                        XMLRefusjon.class,
                        createRefusjon(melding.getRefusjon().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "sykepengerIArbeidsgiverPerioden"),
                        XMLSykepengerIArbeidsgiverperioden.class,
                        createSykepengerIArbeidsgiverperioden(melding.getSykepengerIArbeidsgiverPerioden().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "startdatoForeldrepengerperiode"),
                        LocalDate.class, melding.getStartdatoForeldrepengeperiode().orElse(null)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "opphoerAvNaturalyrelseListe"),
                        XMLOpphoerAvNaturalytelseListe.class,
                        createOpphoerAvNaturalytelseListe(melding.getOpphoerAvNaturalytelseListe().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "gjenopptakelseNaturalytelseListe"),
                        XMLGjenopptakelseNaturalytelseListe.class,
                        createGjenopptakelseNaturalytelseListe(melding.getGjenopptakelseNaturalytelseListe().orElse(null))),
                createAvsendersystem(melding.getAvsendersystem()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "pleiepengerPeriodeListe"),
                        XMLPleiepengerPeriodeListe.class,
                        createPleiepengerPeriodeListe(melding.getPleiepengerPerioder().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "omsorgspenger"),
                        XMLOmsorgspenger.class,
                        createOmsorgspenger(melding.getOmsorgspenger().orElse(null)))),
                Collections.emptyMap());
    }

    private static XMLOmsorgspenger createOmsorgspenger(RsOmsorgspenger omsorgspenger) {
        if (Objects.isNull(omsorgspenger)) { return null; }
        return new XMLOmsorgspenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "harUtbetaltPliktigeDager"), Boolean.class, omsorgspenger.getHarUtbetaltPliktigeDager().orElse(null)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "fravaersPerioder"),
                        XMLFravaersPeriodeListe.class, createFravaersPeriodeListe(omsorgspenger.getFravaersPerioder().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "delvisFravaersListe"),
                        XMLDelvisFravaersListe.class, createDelvisFravaerListe(omsorgspenger.getDelvisFravaersListe().orElse(null))));
    }

    private static XMLFravaersPeriodeListe createFravaersPeriodeListe(List<RsPeriode> perioder) {
        if (Objects.isNull(perioder) || perioder.isEmpty()) { return null; }
        return new XMLFravaersPeriodeListe(
                perioder.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList()));
    }

    private static XMLDelvisFravaersListe createDelvisFravaerListe(List<RsDelvisFravaer> delvisFravaerListe) {
        if (Objects.isNull(delvisFravaerListe) || delvisFravaerListe.isEmpty()) { return null; }
        return new XMLDelvisFravaersListe(
                delvisFravaerListe.stream().map(XmlInntektsmelding201809::createDelvisFravaer).collect(Collectors.toList()));
    }

    private static XMLDelvisFravaer createDelvisFravaer(RsDelvisFravaer delvisFravaer) {
        BigDecimal timer = delvisFravaer.getTimer().map(BigDecimal::valueOf).orElse(null);
        return new XMLDelvisFravaer(
                new JAXBElement<>(new QName(NAMESPACE_URI, "dato"), LocalDate.class, delvisFravaer.getDato().orElse(null)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "timer"), BigDecimal.class, timer));
    }

    private static XMLPleiepengerPeriodeListe createPleiepengerPeriodeListe(List<RsPeriode> perioder) {
        if (Objects.isNull(perioder) || perioder.isEmpty()) { return null; }
        return new XMLPleiepengerPeriodeListe(
                perioder.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList()));
    }

    private static XMLAvsendersystem createAvsendersystem(RsAvsendersystem system) {
        return new XMLAvsendersystem(system.getSystemnavn(), system.getSystemversjon(),
                new JAXBElement<>(new QName(NAMESPACE_URI, "innsendingstidspunkt"),
                        LocalDateTime.class,
                        system.getInnsendingstidspunkt()));
    }

    private static XMLGjenopptakelseNaturalytelseListe createGjenopptakelseNaturalytelseListe(List<RsNaturalytelseDetaljer> liste) {
        if (Objects.isNull(liste) || liste.isEmpty()) { return null; }
        return new XMLGjenopptakelseNaturalytelseListe(
                liste.stream().map(XmlInntektsmelding201809::createNaturalytelse).collect(Collectors.toList()));
    }

    private static XMLOpphoerAvNaturalytelseListe createOpphoerAvNaturalytelseListe(List<RsNaturalytelseDetaljer> liste) {
        if (Objects.isNull(liste) || liste.isEmpty()) { return null; }
        return new XMLOpphoerAvNaturalytelseListe(
                liste.stream().map(XmlInntektsmelding201809::createNaturalytelse).collect(Collectors.toList()));
    }

    private static XMLNaturalytelseDetaljer createNaturalytelse(RsNaturalytelseDetaljer detaljer) {
        BigDecimal beloep = detaljer.getBeloepPrMnd().map(BigDecimal::valueOf).orElse(null);
        return new XMLNaturalytelseDetaljer(
                new JAXBElement<>(new QName(NAMESPACE_URI, "naturalytelseType"), String.class, detaljer.getNaturaytelseType().orElse(null)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, detaljer.getFom().orElse(null)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "beloepPrMnd"), BigDecimal.class, beloep));
    }

    private static XMLSykepengerIArbeidsgiverperioden createSykepengerIArbeidsgiverperioden(RsSykepengerIArbeidsgiverperioden sykepenger) {
        if (Objects.isNull(sykepenger)) { return null; }
        BigDecimal bruttoUtbetalt = sykepenger.getBruttoUtbetalt().map(BigDecimal::valueOf).orElse(null);
        return new XMLSykepengerIArbeidsgiverperioden(
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiverPeriodeListe"),
                        XMLArbeidsgiverperiodeListe.class,
                        createArbeidsgiverperiodeListe(sykepenger.getArbeidsgiverperiodeListe().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "bruttoUtbetalt"), BigDecimal.class, bruttoUtbetalt),
                new JAXBElement<>(new QName(NAMESPACE_URI, "begrunnelseForReduksjonEllerIkkeUtbetalt"),
                        String.class,
                        sykepenger.getBegrunnelseForReduksjonEllerIkkeUtbetalt().orElse(null)));
    }

    private static XMLArbeidsgiverperiodeListe createArbeidsgiverperiodeListe(List<RsPeriode> perioder) {
        if (Objects.isNull(perioder) || perioder.isEmpty()) { return null; }
        return new XMLArbeidsgiverperiodeListe(
                perioder.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList()));
    }

    private static XMLRefusjon createRefusjon(RsRefusjon refusjon) {
        if (Objects.isNull(refusjon)) { return null; }
        BigDecimal belop = refusjon.getRefusjonsbeloepPrMnd().map(BigDecimal::valueOf).orElse(null);
        LocalDate opphoersdato = refusjon.getRefusjonsopphoersdato().orElse(null);
        return new XMLRefusjon(
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class, belop),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsopphoersdato"), LocalDate.class, opphoersdato),
                new JAXBElement<>(
                        new QName(NAMESPACE_URI, "endringIRefusjonListe"),
                        XMLEndringIRefusjonsListe.class,
                        createEndringIRefusjonsListe(refusjon.getEndringIRefusjonListe().orElse(null))));
    }

    private static XMLEndringIRefusjonsListe createEndringIRefusjonsListe(List<RsEndringIRefusjon> liste) {
        if (Objects.isNull(liste) || liste.isEmpty()) { return null; }
        return new XMLEndringIRefusjonsListe(liste.stream().map(XmlInntektsmelding201809::createEndringIRefusjon).collect(Collectors.toList()));
    }

    private static XMLEndringIRefusjon createEndringIRefusjon(RsEndringIRefusjon endring) {
        if (Objects.isNull(endring)) { return null; }
        LocalDate endringsdato = endring.getEndringsdato().orElse(null);
        BigDecimal refusjonsbeloep = endring.getRefusjonsbeloepPrMnd().map(BigDecimal::valueOf).orElse(null);
        return new XMLEndringIRefusjon(
                new JAXBElement<>(new QName(NAMESPACE_URI, "endringsdato"), LocalDate.class, endringsdato),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class, refusjonsbeloep));
    }


    private static XMLArbeidsforhold createArbeidsforhold(RsArbeidsforhold arbeidsforhold) {
        return new XMLArbeidsforhold(
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforholdId"), String.class, arbeidsforhold.getArbeidsforholdId().orElse(null)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "foersteFravaersdag"), LocalDate.class, arbeidsforhold.getFoersteFravaersdag().orElse(null)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "beregnetInntekt"), XMLInntekt.class, createInntekt(arbeidsforhold.getBeregnetInntekt().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "avtaltFerieListe"), XMLAvtaltFerieListe.class, createAvtaltFerieListe(arbeidsforhold.getAvtaltFerieListe().orElse(null))),
                new JAXBElement<>(
                        new QName(NAMESPACE_URI, "utsettelseAvForeldrepengerListe"),
                        XMLUtsettelseAvForeldrepengerListe.class,
                        createUtsettelseAvForeldrepengerListe(arbeidsforhold.getUtsettelseAvForeldrepengerListe().orElse(null))),
                new JAXBElement<>(
                        new QName(NAMESPACE_URI, "graderingIForeldrepengerListe"),
                        XMLGraderingIForeldrepengerListe.class,
                        createGraderingIForeldrepengerListe(arbeidsforhold.getGraderingIForeldrepengerListe().orElse(null))));
    }

    private static XMLGraderingIForeldrepengerListe createGraderingIForeldrepengerListe(List<RsGraderingIForeldrepenger> liste) {
        if (Objects.isNull(liste) || liste.isEmpty()) { return null; }
        return new XMLGraderingIForeldrepengerListe(liste.stream().map(XmlInntektsmelding201809::createGraderingIForeldrepenger).collect(Collectors.toList()));
    }

    private static XMLGraderingIForeldrepenger createGraderingIForeldrepenger(RsGraderingIForeldrepenger gradering) {
        BigInteger arbeidstidprosent = gradering.getArbeidstidprosent().map(BigInteger::valueOf).orElse(null);
        return new XMLGraderingIForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "periode"), XMLPeriode.class, createPeriode(gradering.getPeriode().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidstidprosent"), BigInteger.class, arbeidstidprosent));
    }

    private static XMLUtsettelseAvForeldrepengerListe createUtsettelseAvForeldrepengerListe(List<RsUtsettelseAvForeldrepenger> liste) {
        if (Objects.isNull(liste) || liste.isEmpty()) { return null; }
        return new XMLUtsettelseAvForeldrepengerListe(liste.stream().map(XmlInntektsmelding201809::createUtsettelseAvForeldrepenger).collect(Collectors.toList()));
    }

    private static XMLUtsettelseAvForeldrepenger createUtsettelseAvForeldrepenger(RsUtsettelseAvForeldrepenger utsettelse) {
        return new XMLUtsettelseAvForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "periode"), XMLPeriode.class, createPeriode(utsettelse.getPeriode().orElse(null))),
                new JAXBElement<>(new QName(NAMESPACE_URI, "aarsakTilUtsettelse"), String.class, utsettelse.getAarsakTilUtsettelse().orElse(null)));
    }

    private static XMLAvtaltFerieListe createAvtaltFerieListe(List<RsPeriode> perioder) {
        if (Objects.isNull(perioder) || perioder.isEmpty()) { return null; }
        return new XMLAvtaltFerieListe(perioder.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList()));
    }

    private static XMLPeriode createPeriode(RsPeriode periode) {
        if (Objects.isNull(periode)) { return null; }
        return new XMLPeriode(
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, periode.getFom().orElse(null)),
                new JAXBElement<>(new QName(NAMESPACE_URI, "tom"), LocalDate.class, periode.getTom().orElse(null)));
    }

    private static XMLInntekt createInntekt(RsInntekt inntekt) {
        if (Objects.isNull(inntekt)) { return null; }

        BigDecimal beloep = inntekt.getBeloep().map(BigDecimal::valueOf).orElse(null);
        return new XMLInntekt(
                new JAXBElement<>(new QName(NAMESPACE_URI, "beloep"), BigDecimal.class, beloep),
                new JAXBElement<>(new QName(NAMESPACE_URI, "aarsakVedEndring"), String.class, inntekt.getAarsakVedEndring().orElse(null))
        );
    }

    private static XMLArbeidsgiver createArbeidsgiver(RsArbeidsgiver arbeidsgiver) {
        if (Objects.isNull(arbeidsgiver)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Den forespurte meldingen har ingen arbeidsgiver.");
        }
        return new XMLArbeidsgiver(
                        arbeidsgiver.getVirksomhetsnummer(),
                        new XMLKontaktinformasjon(
                                arbeidsgiver.getKontaktinformasjon().getKontaktinformasjonNavn(),
                                arbeidsgiver.getKontaktinformasjon().getTelefonnummer()));
    }
}
