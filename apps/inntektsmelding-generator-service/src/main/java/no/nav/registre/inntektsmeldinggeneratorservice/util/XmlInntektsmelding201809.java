package no.nav.registre.inntektsmeldinggeneratorservice.util;

import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsforhold;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiver;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsAvsendersystem;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsDelvisFravaer;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsEndringIRefusjon;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsGraderingIForeldrepenger;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntekt;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsNaturalytelseDetaljer;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsOmsorgspenger;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsPeriode;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsRefusjon;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsSykepengerIArbeidsgiverperioden;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsUtsettelseAvForeldrepenger;
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

import static java.util.Objects.isNull;
import static no.nav.registre.inntektsmeldinggeneratorservice.util.XmlConverter.toBigDecimal;
import static no.nav.registre.inntektsmeldinggeneratorservice.util.XmlConverter.toBigInteger;

public class XmlInntektsmelding201809 {

    private XmlInntektsmelding201809() {
    }

    private static final String NAMESPACE_URI = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211";

    public static XMLInntektsmeldingM createInntektsmelding(RsInntektsmelding melding) {
        return new XMLInntektsmeldingM(new XMLSkjemainnhold(
                melding.getYtelse(),
                melding.getAarsakTilInnsending(),
                createArbeidsgiver(melding.getArbeidsgiver()),
                melding.getArbeidstakerFnr(),
                melding.isNaerRelasjon(),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforhold"),
                        XMLArbeidsforhold.class,
                        createArbeidsforhold(melding.getArbeidsforhold())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjon"),
                        XMLRefusjon.class,
                        createRefusjon(melding.getRefusjon())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "sykepengerIArbeidsgiverPerioden"),
                        XMLSykepengerIArbeidsgiverperioden.class,
                        createSykepengerIArbeidsgiverperioden(melding.getSykepengerIArbeidsgiverperioden())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "startdatoForeldrepengerperiode"),
                        LocalDate.class, melding.getStartdatoForeldrepengeperiode()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "opphoerAvNaturalyrelseListe"),
                        XMLOpphoerAvNaturalytelseListe.class,
                        createOpphoerAvNaturalytelseListe(melding.getOpphoerAvNaturalytelseListe())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "gjenopptakelseNaturalytelseListe"),
                        XMLGjenopptakelseNaturalytelseListe.class,
                        createGjenopptakelseNaturalytelseListe(melding.getGjenopptakelseNaturalytelseListe())),
                createAvsendersystem(melding.getAvsendersystem()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "pleiepengerPeriodeListe"),
                        XMLPleiepengerPeriodeListe.class,
                        createPleiepengerPeriodeListe(melding.getPleiepengerPerioder())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "omsorgspenger"),
                        XMLOmsorgspenger.class,
                        createOmsorgspenger(melding.getOmsorgspenger()))),
                Collections.emptyMap());
    }

    private static XMLOmsorgspenger createOmsorgspenger(RsOmsorgspenger omsorgspenger) {

        if (isNull(omsorgspenger)) {
            return null;
        }
        return new XMLOmsorgspenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "harUtbetaltPliktigeDager"), Boolean.class, omsorgspenger.getHarUtbetaltPliktigeDager()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "fravaersPerioder"),
                        XMLFravaersPeriodeListe.class, createFravaersPeriodeListe(omsorgspenger.getFravaersPerioder())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "delvisFravaersListe"),
                        XMLDelvisFravaersListe.class, createDelvisFravaerListe(omsorgspenger.getDelvisFravaersListe())));
    }

    private static XMLFravaersPeriodeListe createFravaersPeriodeListe(List<RsPeriode> perioder) {

        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }
        return new XMLFravaersPeriodeListe(
                perioder.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList()));
    }

    private static XMLDelvisFravaersListe createDelvisFravaerListe(List<RsDelvisFravaer> delvisFravaerListe) {

        if (isNull(delvisFravaerListe) || delvisFravaerListe.isEmpty()) {
            return null;
        }
        return new XMLDelvisFravaersListe(
                delvisFravaerListe.stream().map(XmlInntektsmelding201809::createDelvisFravaer).collect(Collectors.toList()));
    }

    private static XMLDelvisFravaer createDelvisFravaer(RsDelvisFravaer delvisFravaer) {

        return new XMLDelvisFravaer(
                new JAXBElement<>(new QName(NAMESPACE_URI, "dato"), LocalDate.class, delvisFravaer.getDato()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "timer"), BigDecimal.class, toBigDecimal(delvisFravaer.getTimer()))
        );
    }

    private static XMLPleiepengerPeriodeListe createPleiepengerPeriodeListe(List<RsPeriode> perioder) {

        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }

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

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }

        return new XMLGjenopptakelseNaturalytelseListe(
                liste.stream().map(XmlInntektsmelding201809::createNaturalytelse).collect(Collectors.toList()));
    }

    private static XMLOpphoerAvNaturalytelseListe createOpphoerAvNaturalytelseListe(List<RsNaturalytelseDetaljer> liste) {

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }

        return new XMLOpphoerAvNaturalytelseListe(
                liste.stream().map(XmlInntektsmelding201809::createNaturalytelse).collect(Collectors.toList()));
    }

    private static XMLNaturalytelseDetaljer createNaturalytelse(RsNaturalytelseDetaljer detaljer) {

        return new XMLNaturalytelseDetaljer(
                new JAXBElement<>(new QName(NAMESPACE_URI, "naturalytelseType"), String.class, detaljer.getNaturaytelseType()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, detaljer.getFom()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "beloepPrMnd"), BigDecimal.class, toBigDecimal(detaljer.getBeloepPrMnd())));
    }

    private static XMLSykepengerIArbeidsgiverperioden createSykepengerIArbeidsgiverperioden(RsSykepengerIArbeidsgiverperioden sykepenger) {

        if (isNull(sykepenger)) {
            return null;
        }

        return new XMLSykepengerIArbeidsgiverperioden(
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiverPeriodeListe"),
                        XMLArbeidsgiverperiodeListe.class,
                        createArbeidsgiverperiodeListe(sykepenger.getArbeidsgiverperiodeListe())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "bruttoUtbetalt"), BigDecimal.class,
                        toBigDecimal(sykepenger.getBruttoUtbetalt())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "begrunnelseForReduksjonEllerIkkeUtbetalt"),
                        String.class,
                        sykepenger.getBegrunnelseForReduksjonEllerIkkeUtbetalt()));
    }

    private static XMLArbeidsgiverperiodeListe createArbeidsgiverperiodeListe(List<RsPeriode> perioder) {

        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }

        return new XMLArbeidsgiverperiodeListe(
                perioder.stream().map(XmlInntektsmelding201809::createPeriode).toList());
    }

    private static XMLRefusjon createRefusjon(RsRefusjon refusjon) {

        if (isNull(refusjon)) {
            return null;
        }

        return new XMLRefusjon(
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class,
                        toBigDecimal(refusjon.getRefusjonsbeloepPrMnd())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsopphoersdato"), LocalDate.class, refusjon.getRefusjonsopphoersdato()),
                new JAXBElement<>(
                        new QName(NAMESPACE_URI, "endringIRefusjonListe"),
                        XMLEndringIRefusjonsListe.class,
                        createEndringIRefusjonsListe(refusjon.getEndringIRefusjonListe())));
    }

    private static XMLEndringIRefusjonsListe createEndringIRefusjonsListe(List<RsEndringIRefusjon> liste) {

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }
        return new XMLEndringIRefusjonsListe(liste.stream().map(XmlInntektsmelding201809::createEndringIRefusjon).toList());
    }

    private static XMLEndringIRefusjon createEndringIRefusjon(RsEndringIRefusjon endring) {

        if (isNull(endring)) {
            return null;
        }

        return new XMLEndringIRefusjon(
                new JAXBElement<>(new QName(NAMESPACE_URI, "endringsdato"), LocalDate.class, endring.getEndringsdato()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class, toBigDecimal(endring.getRefusjonsbeloepPrMnd()))
        );
    }

    private static XMLArbeidsforhold createArbeidsforhold(RsArbeidsforhold arbeidsforhold) {

        return new XMLArbeidsforhold(
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforholdId"), String.class, arbeidsforhold.getArbeidsforholdId()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "foersteFravaersdag"), LocalDate.class, arbeidsforhold.getFoersteFravaersdag()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "beregnetInntekt"), XMLInntekt.class, createInntekt(arbeidsforhold.getBeregnetInntekt())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "avtaltFerieListe"), XMLAvtaltFerieListe.class, createAvtaltFerieListe(arbeidsforhold.getAvtaltFerieListe())),
                new JAXBElement<>(
                        new QName(NAMESPACE_URI, "utsettelseAvForeldrepengerListe"),
                        XMLUtsettelseAvForeldrepengerListe.class,
                        createUtsettelseAvForeldrepengerListe(arbeidsforhold.getUtsettelseAvForeldrepengerListe())),
                new JAXBElement<>(
                        new QName(NAMESPACE_URI, "graderingIForeldrepengerListe"),
                        XMLGraderingIForeldrepengerListe.class,
                        createGraderingIForeldrepengerListe(arbeidsforhold.getGraderingIForeldrepengerListe())));
    }

    private static XMLGraderingIForeldrepengerListe createGraderingIForeldrepengerListe(List<RsGraderingIForeldrepenger> liste) {

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }

        return new XMLGraderingIForeldrepengerListe(liste.stream().map(XmlInntektsmelding201809::createGraderingIForeldrepenger).toList());
    }

    private static XMLGraderingIForeldrepenger createGraderingIForeldrepenger(RsGraderingIForeldrepenger gradering) {

        return new XMLGraderingIForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "periode"), XMLPeriode.class, createPeriode(gradering.getPeriode())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidstidprosent"), BigInteger.class, toBigInteger(gradering.getArbeidstidprosent())));
    }

    private static XMLUtsettelseAvForeldrepengerListe createUtsettelseAvForeldrepengerListe(List<RsUtsettelseAvForeldrepenger> liste) {
        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }
        return new XMLUtsettelseAvForeldrepengerListe(liste.stream().map(XmlInntektsmelding201809::createUtsettelseAvForeldrepenger).collect(Collectors.toList()));
    }

    private static XMLUtsettelseAvForeldrepenger createUtsettelseAvForeldrepenger(RsUtsettelseAvForeldrepenger utsettelse) {
        return new XMLUtsettelseAvForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "periode"), XMLPeriode.class, createPeriode(utsettelse.getPeriode())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "aarsakTilUtsettelse"), String.class, utsettelse.getAarsakTilUtsettelse()));
    }

    private static XMLAvtaltFerieListe createAvtaltFerieListe(List<RsPeriode> perioder) {
        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }
        return new XMLAvtaltFerieListe(perioder.stream().map(XmlInntektsmelding201809::createPeriode).collect(Collectors.toList()));
    }

    private static XMLPeriode createPeriode(RsPeriode periode) {

        if (isNull(periode)) {
            return null;
        }
        return new XMLPeriode(
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, periode.getFom()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "tom"), LocalDate.class, periode.getTom())
        );
    }

    private static XMLInntekt createInntekt(RsInntekt inntekt) {

        if (isNull(inntekt)) {
            return null;
        }

        return new XMLInntekt(
                new JAXBElement<>(new QName(NAMESPACE_URI, "beloep"), BigDecimal.class, toBigDecimal(inntekt.getBeloep())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "aarsakVedEndring"), String.class, inntekt.getAarsakVedEndring())
        );
    }

    private static XMLArbeidsgiver createArbeidsgiver(RsArbeidsgiver arbeidsgiver) {
        if (isNull(arbeidsgiver)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Den forespurte meldingen har ingen arbeidsgiver.");
        }
        return new XMLArbeidsgiver(
                arbeidsgiver.getVirksomhetsnummer(),
                new XMLKontaktinformasjon(
                        arbeidsgiver.getKontaktinformasjon().getKontaktinformasjonNavn(),
                        arbeidsgiver.getKontaktinformasjon().getTelefonnummer()));
    }
}