package no.nav.testnav.inntektsmeldinggeneratorservice.util;

import io.swagger.v3.core.util.Json;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Arbeidsforhold;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Arbeidsgiver;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.ArbeidsgiverPrivat;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.ArbeidsgiverperiodeListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Avsendersystem;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.AvtaltFerieListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.DelvisFravaer;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.DelvisFravaersListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.EndringIRefusjon;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.EndringIRefusjonsListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.GjenopptakelseNaturalytelseListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.GraderingIForeldrepenger;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.GraderingIForeldrepengerListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Inntekt;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.NaturalytelseDetaljer;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Omsorgspenger;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.OpphoerAvNaturalytelseListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Periode;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Refusjon;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Skjemainnhold;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.SykepengerIArbeidsgiverperioden;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.UtsettelseAvForeldrepengerListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.Melding;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.FravaersPeriodeListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Kontaktinformasjon;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.PleiepengerPeriodeListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.UtsettelseAvForeldrepenger;
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
import org.apache.commons.lang3.StringUtils;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.inntektsmeldinggeneratorservice.util.XmlConverter.toBigDecimal;
import static no.nav.testnav.inntektsmeldinggeneratorservice.util.XmlConverter.toBigInteger;
import static no.nav.testnav.inntektsmeldinggeneratorservice.util.XmlConverter.toCamelCase;
import static no.nav.testnav.inntektsmeldinggeneratorservice.util.XmlConverter.toLocalDate;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class XmlInntektsmelding201812 {

    private static final String NAMESPACE_URI = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211";

    private XmlInntektsmelding201812() {
    }

    public static Melding createInntektsmelding(RsInntektsmelding melding) {
        Melding inntektsMelding = new Melding(new Skjemainnhold(
                melding.getYtelse(),
                melding.getAarsakTilInnsending(),
                createArbeidsgiver(melding.getArbeidsgiver()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsgiverPrivat"),
                        ArbeidsgiverPrivat.class,
                        createArbeidsgiverPrivat(melding.getArbeidsgiverPrivat())),
                melding.getArbeidstakerFnr(),
                melding.isNaerRelasjon(),
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforhold"),
                        Arbeidsforhold.class,
                        createArbeidsforhold(melding.getArbeidsforhold())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "refusjon"),
                        Refusjon.class,
                        createRefusjon(melding.getRefusjon())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "sykepengerIArbeidsgiverPerioden"),
                        SykepengerIArbeidsgiverperioden.class,
                        createSykepengerIArbeidsgiverperioden(melding.getSykepengerIArbeidsgiverperioden())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "startdatoForeldrepengerperiode"),
                        LocalDate.class, nonNull(melding.getStartdatoForeldrepengeperiode()) ?
                        LocalDate.parse(melding.getStartdatoForeldrepengeperiode()) : null),
                new JAXBElement<>(new QName(NAMESPACE_URI, "opphoerAvNaturalyrelseListe"),
                        OpphoerAvNaturalytelseListe.class,
                        createOpphoerAvNaturalytelseListe(melding.getOpphoerAvNaturalytelseListe())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "gjenopptakelseNaturalytelseListe"),
                        GjenopptakelseNaturalytelseListe.class,
                        createGjenopptakelseNaturalytelseListe(melding.getGjenopptakelseNaturalytelseListe())),
                createAvsendersystem(melding.getAvsendersystem()),
                new JAXBElement<>(new QName(NAMESPACE_URI, "pleiepengerPeriodeListe"),
                        PleiepengerPeriodeListe.class,
                        createPleiepengerPeriodeListe(melding.getPleiepengerPerioder())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "omsorgspenger"),
                        Omsorgspenger.class,
                        createOmsorgspenger(melding.getOmsorgspenger()))),
                Collections.emptyMap());
        log.info("Opprettet inntektsmelding med verdier: {}", Json.pretty(inntektsMelding));
        return inntektsMelding;
    }

    private static Omsorgspenger createOmsorgspenger(RsOmsorgspenger omsorgspenger) {

        return isNull(omsorgspenger) ? null :
                new Omsorgspenger(
                        new JAXBElement<>(new QName(NAMESPACE_URI, "harUtbetaltPliktigeDager"), Boolean.class,
                                omsorgspenger.getHarUtbetaltPliktigeDager()),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "fravaersPerioder"),
                                FravaersPeriodeListe.class, createFravaersPeriodeListe(omsorgspenger.getFravaersPerioder())),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "delvisFravaersListe"),
                                DelvisFravaersListe.class, createDelvisFravaerListe(omsorgspenger.getDelvisFravaersListe())));
    }

    private static FravaersPeriodeListe createFravaersPeriodeListe(List<RsPeriode> perioder) {

        if (isNull(perioder) || perioder.isEmpty()) {
            return null;
        }
        return new FravaersPeriodeListe(
                perioder.stream().map(XmlInntektsmelding201812::createPeriode).collect(Collectors.toList()));
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
                new JAXBElement<>(new QName(NAMESPACE_URI, "dato"), LocalDate.class,
                        nonNull(delvisFravaer.getDato()) ? LocalDate.parse(delvisFravaer.getDato()) : null),
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
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, isNotBlank(detaljer.getFom()) ?
                        LocalDate.parse(detaljer.getFom()) : null),
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
                        new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsopphoersdato"), LocalDate.class,
                                isNotBlank(refusjon.getRefusjonsopphoersdato()) ? LocalDate.parse(refusjon.getRefusjonsopphoersdato()) : null),
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
                        new JAXBElement<>(new QName(NAMESPACE_URI, "endringsdato"), LocalDate.class,
                                isNotBlank(endring.getEndringsdato()) ? LocalDate.parse(endring.getEndringsdato()) : null),
                        new JAXBElement<>(new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd"), BigDecimal.class,
                                toBigDecimal(endring.getRefusjonsbeloepPrMnd())));
    }


    private static Arbeidsforhold createArbeidsforhold(RsArbeidsforhold arbeidsforhold) {

        return new Arbeidsforhold(
                new JAXBElement<>(new QName(NAMESPACE_URI, "arbeidsforholdId"), String.class,
                        toCamelCase(arbeidsforhold.getArbeidsforholdId())),
                new JAXBElement<>(new QName(NAMESPACE_URI, "foersteFravaersdag"), LocalDate.class, nonNull(arbeidsforhold.getFoersteFravaersdag()) ?
                        LocalDate.parse(arbeidsforhold.getFoersteFravaersdag()) : null),
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
                new JAXBElement<>(new QName(NAMESPACE_URI, "fom"), LocalDate.class, isNotBlank(periode.getFom()) ?
                        LocalDate.parse(periode.getFom()) : null),
                new JAXBElement<>(new QName(NAMESPACE_URI, "tom"), LocalDate.class, isNotBlank(periode.getTom()) ?
                        LocalDate.parse(periode.getTom()) : null));
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
