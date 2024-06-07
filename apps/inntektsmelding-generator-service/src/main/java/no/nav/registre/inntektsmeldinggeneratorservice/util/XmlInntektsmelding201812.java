package no.nav.registre.inntektsmeldinggeneratorservice.util;

import io.swagger.v3.core.util.Json;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntektsmeldinggeneratorservice.provider.Melding;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsforhold;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiver;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiverPrivat;
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
import no.seres.xsd.nav.inntektsmelding_m._20181211.Arbeidsforhold;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Arbeidsgiver;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ArbeidsgiverPrivat;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ArbeidsgiverperiodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Avsendersystem;
import no.seres.xsd.nav.inntektsmelding_m._20181211.AvtaltFerieListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.DelvisFravaer;
import no.seres.xsd.nav.inntektsmelding_m._20181211.DelvisFravaersListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.EndringIRefusjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.EndringIRefusjonsListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.FravaersPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.GjenopptakelseNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.GraderingIForeldrepenger;
import no.seres.xsd.nav.inntektsmelding_m._20181211.GraderingIForeldrepengerListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Inntekt;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Kontaktinformasjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.NaturalytelseDetaljer;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Omsorgspenger;
import no.seres.xsd.nav.inntektsmelding_m._20181211.OpphoerAvNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Periode;
import no.seres.xsd.nav.inntektsmelding_m._20181211.PleiepengerPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Refusjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Skjemainnhold;
import no.seres.xsd.nav.inntektsmelding_m._20181211.SykepengerIArbeidsgiverperioden;
import no.seres.xsd.nav.inntektsmelding_m._20181211.UtsettelseAvForeldrepenger;
import no.seres.xsd.nav.inntektsmelding_m._20181211.UtsettelseAvForeldrepengerListe;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.registre.inntektsmeldinggeneratorservice.util.XmlConverter.toBigDecimal;
import static no.nav.registre.inntektsmeldinggeneratorservice.util.XmlConverter.toBigInteger;
import static no.nav.registre.inntektsmeldinggeneratorservice.util.XmlConverter.toCamelCase;
import static no.nav.registre.inntektsmeldinggeneratorservice.util.XmlConverter.toLocalDate;

@Slf4j
public class XmlInntektsmelding201812 {

    private static final String NAMESPACE_URI = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211";

    private XmlInntektsmelding201812() {
    }

    public static Melding createInntektsmelding(RsInntektsmelding melding) throws DatatypeConfigurationException {
        var skjemainnhold = new Skjemainnhold();
        skjemainnhold.setYtelse(melding.getYtelse());
        skjemainnhold.setAarsakTilInnsending(melding.getAarsakTilInnsending());
        skjemainnhold.setArbeidsgiver(new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiver"),
                Arbeidsgiver.class,
                createArbeidsgiver(melding.getArbeidsgiver())));
        skjemainnhold.setArbeidsgiverPrivat(new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiverPrivat"),
                ArbeidsgiverPrivat.class,
                createArbeidsgiverPrivat(melding.getArbeidsgiverPrivat())));

        skjemainnhold.setArbeidstakerFnr(melding.getArbeidstakerFnr());
        skjemainnhold.setNaerRelasjon(melding.isNaerRelasjon());
        skjemainnhold.setArbeidsforhold(new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforhold"),
                Arbeidsforhold.class,
                createArbeidsforhold(melding.getArbeidsforhold())));
        skjemainnhold.setRefusjon(new JAXBElement<>(new QName(NAMESPACE_URI, "refusjon"),
                Refusjon.class,
                createRefusjon(melding.getRefusjon())));
        skjemainnhold.setSykepengerIArbeidsgiverperioden(new JAXBElement<>(new QName(NAMESPACE_URI, "sykepengerIArbeidsgiverPerioden"),
                SykepengerIArbeidsgiverperioden.class,
                createSykepengerIArbeidsgiverperioden(melding.getSykepengerIArbeidsgiverperioden())));


        LocalDate localDate = toLocalDate(melding.getStartdatoForeldrepengeperiode());
        if (localDate != null) {
            var gregorianCalendar = GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            var xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            skjemainnhold.setStartdatoForeldrepengeperiode(new JAXBElement<>(new QName(NAMESPACE_URI, "startdatoForeldrepengerperiode"),
                    XMLGregorianCalendar.class, xmlGregorianCalendar));
        }

        skjemainnhold.setOpphoerAvNaturalytelseListe(new JAXBElement<>(new QName(NAMESPACE_URI, "opphoerAvNaturalyrelseListe"),
                OpphoerAvNaturalytelseListe.class,
                createOpphoerAvNaturalytelseListe(melding.getOpphoerAvNaturalytelseListe())));
        skjemainnhold.setGjenopptakelseNaturalytelseListe(new JAXBElement<>(new QName(NAMESPACE_URI, "gjenopptakelseNaturalytelseListe"),
                GjenopptakelseNaturalytelseListe.class,
                createGjenopptakelseNaturalytelseListe(melding.getGjenopptakelseNaturalytelseListe())));
        skjemainnhold.setAvsendersystem(createAvsendersystem(melding.getAvsendersystem()));
        skjemainnhold.setPleiepengerPerioder(new JAXBElement<>(new QName(NAMESPACE_URI, "pleiepengerPeriodeListe"),
                PleiepengerPeriodeListe.class,
                createPleiepengerPeriodeListe(melding.getPleiepengerPerioder())));
        skjemainnhold.setOmsorgspenger(new JAXBElement<>(new QName(NAMESPACE_URI, "omsorgspenger"),
                Omsorgspenger.class,
                createOmsorgspenger(melding.getOmsorgspenger())));
        Melding inntektsMelding = new Melding(
                skjemainnhold,
                Collections.emptyMap());
        log.info("Opprettet inntektsmelding med verdier: {}", Json.pretty(inntektsMelding));
        return inntektsMelding;
    }

    private static Omsorgspenger createOmsorgspenger(RsOmsorgspenger omsorgspenger) {

        if (isNull(omsorgspenger)) {
            return null;
        }
        var op = new Omsorgspenger();
        op.setDelvisFravaersListe(new JAXBElement<>(new QName(NAMESPACE_URI, "delvisFravaersListe"),
                DelvisFravaersListe.class,
                createDelvisFravaerListe(omsorgspenger.getDelvisFravaersListe())));
        op.setFravaersPerioder(new JAXBElement<>(new QName(NAMESPACE_URI, "fravaersPerioder"),
                FravaersPeriodeListe.class,
                createFravaersPeriodeListe(omsorgspenger.getFravaersPerioder())));
        op.setHarUtbetaltPliktigeDager(new JAXBElement<>(new QName(NAMESPACE_URI, "harUtbetaltPliktigeDager"),
                Boolean.class,
                omsorgspenger.getHarUtbetaltPliktigeDager()));
        return op;
    }

    private static FravaersPeriodeListe createFravaersPeriodeListe(List<RsPeriode> perioder) {

        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }
        var fravaersPeriodeListe = new FravaersPeriodeListe();
        List<Periode> p = perioder.stream().map(XmlInntektsmelding201812::createPeriode).collect(Collectors.toList());
//        fravaersPeriodeListe.setFravaerPeriode(p);
        return fravaersPeriodeListe;
    }

    private static DelvisFravaersListe createDelvisFravaerListe(List<RsDelvisFravaer> delvisFravaerListe) {

        if (isNull(delvisFravaerListe) || delvisFravaerListe.isEmpty()) {
            return null;
        }
        return new DelvisFravaersListe(
                delvisFravaerListe.stream().map(XmlInntektsmelding201812::createDelvisFravaer).collect(Collectors.toList()));
    }

    private static DelvisFravaer createDelvisFravaer(RsDelvisFravaer delvisFravaer) {

        return new DelvisFravaer(
                new JAXBElement<>(new QName(NAMESPACE_URI, "dato"), LocalDate.class, toLocalDate(delvisFravaer.getDato())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "timer"), BigDecimal.class, toBigDecimal(delvisFravaer.getTimer()))
        );
    }

    private static PleiepengerPeriodeListe createPleiepengerPeriodeListe(List<RsPeriode> perioder) {

        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }
        return new PleiepengerPeriodeListe(
                perioder.stream().map(XmlInntektsmelding201812::createPeriode).collect(Collectors.toList()));
    }

    private static Avsendersystem createAvsendersystem(RsAvsendersystem system) {

        return new Avsendersystem(system.getSystemnavn(), system.getSystemversjon(),
                new JAXBElement<>(new QName(NAMESPACE_URI, "innsendingstidspunkt"),
                        LocalDateTime.class,
                        system.getInnsendingstidspunkt()));
    }

    private static GjenopptakelseNaturalytelseListe createGjenopptakelseNaturalytelseListe(List<RsNaturalytelseDetaljer> liste) {

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }
        return new GjenopptakelseNaturalytelseListe(
                liste.stream().map(XmlInntektsmelding201812::createNaturalytelse).collect(Collectors.toList()));
    }

    private static OpphoerAvNaturalytelseListe createOpphoerAvNaturalytelseListe(List<RsNaturalytelseDetaljer> liste) {

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }
        return new OpphoerAvNaturalytelseListe(
                liste.stream().map(XmlInntektsmelding201812::createNaturalytelse).collect(Collectors.toList()));
    }

    private static NaturalytelseDetaljer createNaturalytelse(RsNaturalytelseDetaljer detaljer) {

        return new NaturalytelseDetaljer(
                new JAXBElement<>(new QName(NAMESPACE_URI, "naturalytelseType"), String.class,
                        toCamelCase(detaljer.getNaturalytelseType())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, toLocalDate(detaljer.getFom())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "beloepPrMnd"), BigDecimal.class, toBigDecimal(detaljer.getBeloepPrMnd()))
        );
    }

    private static SykepengerIArbeidsgiverperioden createSykepengerIArbeidsgiverperioden(RsSykepengerIArbeidsgiverperioden sykepenger) {

        return isNull(sykepenger) ? null :
                new SykepengerIArbeidsgiverperioden(
                        new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiverPeriodeListe"),
                                ArbeidsgiverperiodeListe.class,
                                createArbeidsgiverperiodeListe(sykepenger.getArbeidsgiverperiodeListe())),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "bruttoUtbetalt"), BigDecimal.class, toBigDecimal(sykepenger.getBruttoUtbetalt())),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "begrunnelseForReduksjonEllerIkkeUtbetalt"),
                                String.class,
                                toCamelCase(sykepenger.getBegrunnelseForReduksjonEllerIkkeUtbetalt()))
                );
    }

    private static ArbeidsgiverperiodeListe createArbeidsgiverperiodeListe(List<RsPeriode> perioder) {

        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }
        return new ArbeidsgiverperiodeListe(
                perioder.stream().map(XmlInntektsmelding201812::createPeriode).toList());
    }

    private static Refusjon createRefusjon(RsRefusjon refusjon) {

        return isNull(refusjon) ? null :
                new Refusjon(
                        new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class, toBigDecimal(refusjon.getRefusjonsbeloepPrMnd())),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsopphoersdato"), LocalDate.class, toLocalDate(refusjon.getRefusjonsopphoersdato())),
                        new JAXBElement<>(
                                new QName(NAMESPACE_URI, "endringIRefusjonListe"),
                                EndringIRefusjonsListe.class,
                                createEndringIRefusjonsListe(refusjon.getEndringIRefusjonListe())));
    }

    private static EndringIRefusjonsListe createEndringIRefusjonsListe(List<RsEndringIRefusjon> liste) {

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }

        return new EndringIRefusjonsListe(liste.stream().map(XmlInntektsmelding201812::createEndringIRefusjon).toList());
    }

    private static EndringIRefusjon createEndringIRefusjon(RsEndringIRefusjon endring) {

        return isNull(endring) ? null :
                new EndringIRefusjon(
                        new JAXBElement<>(new QName(NAMESPACE_URI, "endringsdato"), LocalDate.class, toLocalDate(endring.getEndringsdato())),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class,
                                toBigDecimal(endring.getRefusjonsbeloepPrMnd())));
    }


    private static Arbeidsforhold createArbeidsforhold(RsArbeidsforhold arbeidsforhold) {

        return new Arbeidsforhold(
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforholdId"), String.class,
                        toCamelCase(arbeidsforhold.getArbeidsforholdId())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "foersteFravaersdag"), LocalDate.class, toLocalDate(arbeidsforhold.getFoersteFravaersdag())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "beregnetInntekt"), Inntekt.class, createInntekt(arbeidsforhold.getBeregnetInntekt())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "avtaltFerieListe"), AvtaltFerieListe.class, createAvtaltFerieListe(arbeidsforhold.getAvtaltFerieListe())),
                new JAXBElement<>(
                        new QName(NAMESPACE_URI, "utsettelseAvForeldrepengerListe"),
                        UtsettelseAvForeldrepengerListe.class,
                        createUtsettelseAvForeldrepengerListe(arbeidsforhold.getUtsettelseAvForeldrepengerListe())),
                new JAXBElement<>(
                        new QName(NAMESPACE_URI, "graderingIForeldrepengerListe"),
                        GraderingIForeldrepengerListe.class,
                        createGraderingIForeldrepengerListe(arbeidsforhold.getGraderingIForeldrepengerListe())));
    }

    private static GraderingIForeldrepengerListe createGraderingIForeldrepengerListe(List<RsGraderingIForeldrepenger> liste) {

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }

        return new GraderingIForeldrepengerListe(liste.stream().map(XmlInntektsmelding201812::createGraderingIForeldrepenger).toList());
    }

    private static GraderingIForeldrepenger createGraderingIForeldrepenger(RsGraderingIForeldrepenger gradering) {

        return new GraderingIForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "periode"), Periode.class, createPeriode(gradering.getPeriode())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidstidprosent"), BigInteger.class,
                        toBigInteger(gradering.getArbeidstidprosent())));
    }

    private static UtsettelseAvForeldrepengerListe createUtsettelseAvForeldrepengerListe(List<RsUtsettelseAvForeldrepenger> liste) {

        if (isNull(liste) || liste.isEmpty()) {
            return null;
        }

        return new UtsettelseAvForeldrepengerListe(liste.stream().map(XmlInntektsmelding201812::createUtsettelseAvForeldrepenger).toList());
    }

    private static UtsettelseAvForeldrepenger createUtsettelseAvForeldrepenger(RsUtsettelseAvForeldrepenger utsettelse) {

        return new UtsettelseAvForeldrepenger(
                new JAXBElement<>(new QName(NAMESPACE_URI, "periode"), Periode.class, createPeriode(utsettelse.getPeriode())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "aarsakTilUtsettelse"), String.class,
                        toCamelCase(utsettelse.getAarsakTilUtsettelse()))
        );
    }

    private static AvtaltFerieListe createAvtaltFerieListe(List<RsPeriode> perioder) {

        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }

        return new AvtaltFerieListe(perioder.stream().map(XmlInntektsmelding201812::createPeriode).toList());
    }

    private static Periode createPeriode(RsPeriode periode) {

        if (isNull(periode)) {
            return null;
        }

        return new Periode(
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, toLocalDate(periode.getFom())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "tom"), LocalDate.class, toLocalDate(periode.getTom())));
    }

    private static Inntekt createInntekt(RsInntekt inntekt) {

        if (isNull(inntekt)) {
            return null;
        }

        return new Inntekt(
                new JAXBElement<>(new QName(NAMESPACE_URI, "beloep"), BigDecimal.class,
                        toBigDecimal(inntekt.getBeloep())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "aarsakVedEndring"), String.class,
                        toCamelCase(inntekt.getAarsakVedEndring()))
        );
    }

    private static ArbeidsgiverPrivat createArbeidsgiverPrivat(RsArbeidsgiverPrivat arbeidsgiver) {

        return isNull(arbeidsgiver) ? null :
                new ArbeidsgiverPrivat(
                        arbeidsgiver.getArbeidsgiverFnr(),
                        new Kontaktinformasjon(
                                arbeidsgiver.getKontaktinformasjon().getKontaktinformasjonNavn(),
                                arbeidsgiver.getKontaktinformasjon().getTelefonnummer()));
    }

    private static Arbeidsgiver createArbeidsgiver(RsArbeidsgiver arbeidsgiver) {

        return isNull(arbeidsgiver) ? null :
                new Arbeidsgiver(
                        arbeidsgiver.getVirksomhetsnummer(),
                        new Kontaktinformasjon(
                                arbeidsgiver.getKontaktinformasjon().getKontaktinformasjonNavn(),
                                arbeidsgiver.getKontaktinformasjon().getTelefonnummer()));
    }
}
