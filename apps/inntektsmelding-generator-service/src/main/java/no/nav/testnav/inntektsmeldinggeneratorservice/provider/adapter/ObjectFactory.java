//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.FravaersPeriodeListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.GjenopptakelseNaturalytelseListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.GraderingIForeldrepenger;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.GraderingIForeldrepengerListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Inntekt;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.InntektsmeldingM;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Kontaktinformasjon;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.NaturalytelseDetaljer;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Omsorgspenger;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.OpphoerAvNaturalytelseListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Periode;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.PleiepengerPeriodeListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Refusjon;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Skjemainnhold;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.SykepengerIArbeidsgiverperioden;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.UtsettelseAvForeldrepenger;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.UtsettelseAvForeldrepengerListe;
import org.apache.commons.lang3.StringUtils;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@XmlRegistry
public class ObjectFactory {
    private static final String NAMESPACE_URI = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211";

    private static final QName _Melding_QNAME = new QName(NAMESPACE_URI, "melding");
    private static final QName _EndringIRefusjonRefusjonsbeloepPrMnd_QNAME = new QName(NAMESPACE_URI, "refusjonsbeloepPrMnd");
    private static final QName _EndringIRefusjonEndringsdato_QNAME = new QName(NAMESPACE_URI, "endringsdato");
    private static final QName _UtsettelseAvForeldrepengerPeriode_QNAME = new QName(NAMESPACE_URI, "periode");
    private static final QName _UtsettelseAvForeldrepengerAarsakTilUtsettelse_QNAME = new QName(NAMESPACE_URI, "aarsakTilUtsettelse");
    private static final QName _AvsendersystemInnsendingstidspunkt_QNAME = new QName(NAMESPACE_URI, "innsendingstidspunkt");
    private static final QName _SkjemainnholdArbeidsforhold_QNAME = new QName(NAMESPACE_URI, "arbeidsforhold");
    private static final QName _SkjemainnholdRefusjon_QNAME = new QName(NAMESPACE_URI, "refusjon");
    private static final QName _SkjemainnholdSykepengerIArbeidsgiverperioden_QNAME = new QName(NAMESPACE_URI, "sykepengerIArbeidsgiverperioden");
    private static final QName _SkjemainnholdGjenopptakelseNaturalytelseListe_QNAME = new QName(NAMESPACE_URI, "gjenopptakelseNaturalytelseListe");
    private static final QName _SkjemainnholdOmsorgspenger_QNAME = new QName(NAMESPACE_URI, "omsorgspenger");
    private static final QName _SkjemainnholdStartdatoForeldrepengeperiode_QNAME = new QName(NAMESPACE_URI, "startdatoForeldrepengeperiode");
    private static final QName _SkjemainnholdOpphoerAvNaturalytelseListe_QNAME = new QName(NAMESPACE_URI, "opphoerAvNaturalytelseListe");
    private static final QName _SkjemainnholdPleiepengerPerioder_QNAME = new QName(NAMESPACE_URI, "pleiepengerPerioder");
    private static final QName _SkjemainnholdArbeidsgiverPrivat_QNAME = new QName(NAMESPACE_URI, "arbeidsgiverPrivat");
    private static final QName _SykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe_QNAME = new QName(NAMESPACE_URI, "arbeidsgiverperiodeListe");
    private static final QName _SykepengerIArbeidsgiverperiodenBruttoUtbetalt_QNAME = new QName(NAMESPACE_URI, "bruttoUtbetalt");
    private static final QName _SykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt_QNAME = new QName(NAMESPACE_URI, "begrunnelseForReduksjonEllerIkkeUtbetalt");
    private static final QName _OmsorgspengerFravaersPerioder_QNAME = new QName(NAMESPACE_URI, "fravaersPerioder");
    private static final QName _OmsorgspengerHarUtbetaltPliktigeDager_QNAME = new QName(NAMESPACE_URI, "harUtbetaltPliktigeDager");
    private static final QName _OmsorgspengerDelvisFravaersListe_QNAME = new QName(NAMESPACE_URI, "delvisFravaersListe");
    private static final QName _GraderingIForeldrepengerArbeidstidprosent_QNAME = new QName(NAMESPACE_URI, "arbeidstidprosent");
    private static final QName _DelvisFravaerDato_QNAME = new QName(NAMESPACE_URI, "dato");
    private static final QName _DelvisFravaerTimer_QNAME = new QName(NAMESPACE_URI, "timer");
    private static final QName _PeriodeFom_QNAME = new QName(NAMESPACE_URI, "fom");
    private static final QName _PeriodeTom_QNAME = new QName(NAMESPACE_URI, "tom");
    private static final QName _InntektBeloep_QNAME = new QName(NAMESPACE_URI, "beloep");
    private static final QName _InntektAarsakVedEndring_QNAME = new QName(NAMESPACE_URI, "aarsakVedEndring");
    private static final QName _ArbeidsforholdFoersteFravaersdag_QNAME = new QName(NAMESPACE_URI, "foersteFravaersdag");
    private static final QName _ArbeidsforholdBeregnetInntekt_QNAME = new QName(NAMESPACE_URI, "beregnetInntekt");
    private static final QName _ArbeidsforholdArbeidsforholdId_QNAME = new QName(NAMESPACE_URI, "arbeidsforholdId");
    private static final QName _ArbeidsforholdGraderingIForeldrepengerListe_QNAME = new QName(NAMESPACE_URI, "graderingIForeldrepengerListe");
    private static final QName _ArbeidsforholdUtsettelseAvForeldrepengerListe_QNAME = new QName(NAMESPACE_URI, "utsettelseAvForeldrepengerListe");
    private static final QName _ArbeidsforholdAvtaltFerieListe_QNAME = new QName(NAMESPACE_URI, "avtaltFerieListe");
    private static final QName _NaturalytelseDetaljerBeloepPrMnd_QNAME = new QName(NAMESPACE_URI, "beloepPrMnd");
    private static final QName _NaturalytelseDetaljerNaturalytelseType_QNAME = new QName(NAMESPACE_URI, "naturalytelseType");
    private static final QName _RefusjonRefusjonsopphoersdato_QNAME = new QName(NAMESPACE_URI, "refusjonsopphoersdato");
    private static final QName _RefusjonEndringIRefusjonListe_QNAME = new QName(NAMESPACE_URI, "endringIRefusjonListe");

    public ObjectFactory() {
    }

    public EndringIRefusjon createEndringIRefusjon() {
        return new EndringIRefusjon();
    }

    public UtsettelseAvForeldrepenger createUtsettelseAvForeldrepenger() {
        return new UtsettelseAvForeldrepenger();
    }

    public SykepengerIArbeidsgiverperioden createSykepengerIArbeidsgiverperioden() {
        return new SykepengerIArbeidsgiverperioden();
    }

    public OpphoerAvNaturalytelseListe createOpphoerAvNaturalytelseListe() {
        return new OpphoerAvNaturalytelseListe();
    }

    public EndringIRefusjonsListe createEndringIRefusjonsListe() {
        return new EndringIRefusjonsListe();
    }

    public GraderingIForeldrepenger createGraderingIForeldrepenger() {
        return new GraderingIForeldrepenger();
    }

    public DelvisFravaer createDelvisFravaer() {
        return new DelvisFravaer();
    }

    public DelvisFravaersListe createDelvisFravaersListe() {
        return new DelvisFravaersListe();
    }

    public UtsettelseAvForeldrepengerListe createUtsettelseAvForeldrepengerListe() {
        return new UtsettelseAvForeldrepengerListe();
    }

    public Arbeidsforhold createArbeidsforhold() {
        return new Arbeidsforhold();
    }

    public GraderingIForeldrepengerListe createGraderingIForeldrepengerListe() {
        return new GraderingIForeldrepengerListe();
    }

    public AvtaltFerieListe createAvtaltFerieListe() {
        return new AvtaltFerieListe();
    }

    public Arbeidsgiver createArbeidsgiver() {
        return new Arbeidsgiver();
    }

    public Avsendersystem createAvsendersystem() {
        return new Avsendersystem();
    }

    public InntektsmeldingM createInntektsmeldingM() {
        return new InntektsmeldingM();
    }

    public Skjemainnhold createSkjemainnhold() {
        return new Skjemainnhold();
    }

    public Omsorgspenger createOmsorgspenger() {
        return new Omsorgspenger();
    }

    public PleiepengerPeriodeListe createPleiepengerPeriodeListe() {
        return new PleiepengerPeriodeListe();
    }

    public GjenopptakelseNaturalytelseListe createGjenopptakelseNaturalytelseListe() {
        return new GjenopptakelseNaturalytelseListe();
    }

    public Kontaktinformasjon createKontaktinformasjon() {
        return new Kontaktinformasjon();
    }

    public ArbeidsgiverPrivat createArbeidsgiverPrivat() {
        return new ArbeidsgiverPrivat();
    }

    public ArbeidsgiverperiodeListe createArbeidsgiverperiodeListe() {
        return new ArbeidsgiverperiodeListe();
    }

    public Inntekt createInntekt() {
        return new Inntekt();
    }

    public Periode createPeriode() {
        return new Periode();
    }

    public Refusjon createRefusjon() {
        return new Refusjon();
    }

    public NaturalytelseDetaljer createNaturalytelseDetaljer() {
        return new NaturalytelseDetaljer();
    }

    public FravaersPeriodeListe createFravaersPeriodeListe() {
        return new FravaersPeriodeListe();
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "melding"
    )
    public JAXBElement<InntektsmeldingM> createMelding(InntektsmeldingM value) {
        return new JAXBElement<>(_Melding_QNAME, InntektsmeldingM.class, null, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "refusjonsbeloepPrMnd",
            scope = EndringIRefusjon.class
    )
    public JAXBElement createEndringIRefusjonRefusjonsbeloepPrMnd(BigDecimal value) {
        return new JAXBElement<>(_EndringIRefusjonRefusjonsbeloepPrMnd_QNAME, BigDecimal.class, EndringIRefusjon.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "endringsdato",
            scope = EndringIRefusjon.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createEndringIRefusjonEndringsdato(String value) {
        return new JAXBElement<>(_EndringIRefusjonEndringsdato_QNAME, LocalDate.class, EndringIRefusjon.class,
                isNotBlank(value) ? LocalDate.parse(value) : null);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "periode",
            scope = UtsettelseAvForeldrepenger.class
    )
    public JAXBElement createUtsettelseAvForeldrepengerPeriode(Periode value) {
        return new JAXBElement<>(_UtsettelseAvForeldrepengerPeriode_QNAME, Periode.class, UtsettelseAvForeldrepenger.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "aarsakTilUtsettelse",
            scope = UtsettelseAvForeldrepenger.class
    )
    public JAXBElement createUtsettelseAvForeldrepengerAarsakTilUtsettelse(String value) {
        return new JAXBElement<>(_UtsettelseAvForeldrepengerAarsakTilUtsettelse_QNAME, String.class, UtsettelseAvForeldrepenger.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "innsendingstidspunkt",
            scope = Avsendersystem.class
    )
    @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter.class)
    public JAXBElement createAvsendersystemInnsendingstidspunkt(LocalDateTime value) {
        return new JAXBElement<>(_AvsendersystemInnsendingstidspunkt_QNAME, LocalDateTime.class, Avsendersystem.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "arbeidsforhold",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdArbeidsforhold(Arbeidsforhold value) {
        return new JAXBElement<>(_SkjemainnholdArbeidsforhold_QNAME, Arbeidsforhold.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "refusjon",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdRefusjon(Refusjon value) {
        return new JAXBElement<>(_SkjemainnholdRefusjon_QNAME, Refusjon.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "sykepengerIArbeidsgiverperioden",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdSykepengerIArbeidsgiverperioden(SykepengerIArbeidsgiverperioden value) {
        return new JAXBElement<>(_SkjemainnholdSykepengerIArbeidsgiverperioden_QNAME, SykepengerIArbeidsgiverperioden.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "gjenopptakelseNaturalytelseListe",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdGjenopptakelseNaturalytelseListe(GjenopptakelseNaturalytelseListe value) {
        return new JAXBElement<>(_SkjemainnholdGjenopptakelseNaturalytelseListe_QNAME, GjenopptakelseNaturalytelseListe.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "omsorgspenger",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdOmsorgspenger(Omsorgspenger value) {
        return new JAXBElement<>(_SkjemainnholdOmsorgspenger_QNAME, Omsorgspenger.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "startdatoForeldrepengeperiode",
            scope = Skjemainnhold.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createSkjemainnholdStartdatoForeldrepengeperiode(LocalDate value) {
        return new JAXBElement<>(_SkjemainnholdStartdatoForeldrepengeperiode_QNAME, LocalDate.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "opphoerAvNaturalytelseListe",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdOpphoerAvNaturalytelseListe(OpphoerAvNaturalytelseListe value) {
        return new JAXBElement<>(_SkjemainnholdOpphoerAvNaturalytelseListe_QNAME, OpphoerAvNaturalytelseListe.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "pleiepengerPerioder",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdPleiepengerPerioder(PleiepengerPeriodeListe value) {
        return new JAXBElement<>(_SkjemainnholdPleiepengerPerioder_QNAME, PleiepengerPeriodeListe.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "arbeidsgiverPrivat",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdArbeidsgiverPrivat(ArbeidsgiverPrivat value) {
        return new JAXBElement<>(_SkjemainnholdArbeidsgiverPrivat_QNAME, ArbeidsgiverPrivat.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "arbeidsgiverperiodeListe",
            scope = SykepengerIArbeidsgiverperioden.class
    )
    public JAXBElement createSykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe(ArbeidsgiverperiodeListe value) {
        return new JAXBElement<>(_SykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe_QNAME, ArbeidsgiverperiodeListe.class, SykepengerIArbeidsgiverperioden.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "bruttoUtbetalt",
            scope = SykepengerIArbeidsgiverperioden.class
    )
    public JAXBElement createSykepengerIArbeidsgiverperiodenBruttoUtbetalt(BigDecimal value) {
        return new JAXBElement<>(_SykepengerIArbeidsgiverperiodenBruttoUtbetalt_QNAME, BigDecimal.class, SykepengerIArbeidsgiverperioden.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "begrunnelseForReduksjonEllerIkkeUtbetalt",
            scope = SykepengerIArbeidsgiverperioden.class
    )
    public JAXBElement createSykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt(String value) {
        return new JAXBElement<>(_SykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt_QNAME, String.class, SykepengerIArbeidsgiverperioden.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "fravaersPerioder",
            scope = Omsorgspenger.class
    )
    public JAXBElement createOmsorgspengerFravaersPerioder(FravaersPeriodeListe value) {
        return new JAXBElement<>(_OmsorgspengerFravaersPerioder_QNAME, FravaersPeriodeListe.class, Omsorgspenger.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "harUtbetaltPliktigeDager",
            scope = Omsorgspenger.class
    )
    public JAXBElement createOmsorgspengerHarUtbetaltPliktigeDager(Boolean value) {
        return new JAXBElement<>(_OmsorgspengerHarUtbetaltPliktigeDager_QNAME, Boolean.class, Omsorgspenger.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "delvisFravaersListe",
            scope = Omsorgspenger.class
    )
    public JAXBElement createOmsorgspengerDelvisFravaersListe(DelvisFravaersListe value) {
        return new JAXBElement<>(_OmsorgspengerDelvisFravaersListe_QNAME, DelvisFravaersListe.class, Omsorgspenger.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "periode",
            scope = GraderingIForeldrepenger.class
    )
    public JAXBElement createGraderingIForeldrepengerPeriode(Periode value) {
        return new JAXBElement<>(_UtsettelseAvForeldrepengerPeriode_QNAME, Periode.class, GraderingIForeldrepenger.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "arbeidstidprosent",
            scope = GraderingIForeldrepenger.class
    )
    public JAXBElement createGraderingIForeldrepengerArbeidstidprosent(BigInteger value) {
        return new JAXBElement<>(_GraderingIForeldrepengerArbeidstidprosent_QNAME, BigInteger.class, GraderingIForeldrepenger.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "dato",
            scope = DelvisFravaer.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createDelvisFravaerDato(String value) {
        return new JAXBElement<>(_DelvisFravaerDato_QNAME, LocalDate.class, DelvisFravaer.class, isNotBlank(value) ?
                LocalDate.parse(value) : null);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "timer",
            scope = DelvisFravaer.class
    )
    public JAXBElement createDelvisFravaerTimer(BigDecimal value) {
        return new JAXBElement<>(_DelvisFravaerTimer_QNAME, BigDecimal.class, DelvisFravaer.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "fom",
            scope = Periode.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createPeriodeFom(String value) {
        return new JAXBElement<>(_PeriodeFom_QNAME, LocalDate.class, Periode.class,
                isNotBlank(value) ? LocalDate.parse(value) : null);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "tom",
            scope = Periode.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createPeriodeTom(String value) {
        return new JAXBElement<>(_PeriodeTom_QNAME, LocalDate.class, Periode.class,
                isNotBlank(value) ? LocalDate.parse(value) : null);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "beloep",
            scope = Inntekt.class
    )
    public JAXBElement createInntektBeloep(BigDecimal value) {
        return new JAXBElement<>(_InntektBeloep_QNAME, BigDecimal.class, Inntekt.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "aarsakVedEndring",
            scope = Inntekt.class
    )
    public JAXBElement createInntektAarsakVedEndring(String value) {
        return new JAXBElement<>(_InntektAarsakVedEndring_QNAME, String.class, Inntekt.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "foersteFravaersdag",
            scope = Arbeidsforhold.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createArbeidsforholdFoersteFravaersdag(String value) {
        return new JAXBElement<>(_ArbeidsforholdFoersteFravaersdag_QNAME, LocalDate.class, Arbeidsforhold.class,
                isNotBlank(value) ? LocalDate.parse(value) : null);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "beregnetInntekt",
            scope = Arbeidsforhold.class
    )
    public JAXBElement<Inntekt> createArbeidsforholdBeregnetInntekt(Inntekt value) {
        return new JAXBElement<>(_ArbeidsforholdBeregnetInntekt_QNAME, Inntekt.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "arbeidsforholdId",
            scope = Arbeidsforhold.class
    )
    public JAXBElement<String> createArbeidsforholdArbeidsforholdId(String value) {
        return new JAXBElement<>(_ArbeidsforholdArbeidsforholdId_QNAME, String.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "graderingIForeldrepengerListe",
            scope = Arbeidsforhold.class
    )
    public JAXBElement<GraderingIForeldrepengerListe> createArbeidsforholdGraderingIForeldrepengerListe(GraderingIForeldrepengerListe value) {
        return new JAXBElement<>(_ArbeidsforholdGraderingIForeldrepengerListe_QNAME, GraderingIForeldrepengerListe.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "utsettelseAvForeldrepengerListe",
            scope = Arbeidsforhold.class
    )
    public JAXBElement<UtsettelseAvForeldrepengerListe> createArbeidsforholdUtsettelseAvForeldrepengerListe(UtsettelseAvForeldrepengerListe value) {
        return new JAXBElement<>(_ArbeidsforholdUtsettelseAvForeldrepengerListe_QNAME, UtsettelseAvForeldrepengerListe.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "avtaltFerieListe",
            scope = Arbeidsforhold.class
    )
    public JAXBElement<AvtaltFerieListe> createArbeidsforholdAvtaltFerieListe(AvtaltFerieListe value) {
        return new JAXBElement<>(_ArbeidsforholdAvtaltFerieListe_QNAME, AvtaltFerieListe.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "beloepPrMnd",
            scope = NaturalytelseDetaljer.class
    )
    public JAXBElement<BigDecimal> createNaturalytelseDetaljerBeloepPrMnd(BigDecimal value) {
        return new JAXBElement<>(_NaturalytelseDetaljerBeloepPrMnd_QNAME, BigDecimal.class, NaturalytelseDetaljer.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "fom",
            scope = NaturalytelseDetaljer.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement<LocalDate> createNaturalytelseDetaljerFom(String value) {
        return new JAXBElement<>(_PeriodeFom_QNAME, LocalDate.class, NaturalytelseDetaljer.class,
                isNotBlank(value) ? LocalDate.parse(value) : null);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "naturalytelseType",
            scope = NaturalytelseDetaljer.class
    )
    public JAXBElement<String> createNaturalytelseDetaljerNaturalytelseType(String value) {
        return new JAXBElement<>(_NaturalytelseDetaljerNaturalytelseType_QNAME, String.class, NaturalytelseDetaljer.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "refusjonsopphoersdato",
            scope = Refusjon.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement<LocalDate> createRefusjonRefusjonsopphoersdato(String value) {
        return new JAXBElement<>(_RefusjonRefusjonsopphoersdato_QNAME, LocalDate.class, Refusjon.class,
                isNotBlank(value) ? LocalDate.parse(value) : null);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "refusjonsbeloepPrMnd",
            scope = Refusjon.class
    )
    public JAXBElement<BigDecimal> createRefusjonRefusjonsbeloepPrMnd(BigDecimal value) {
        return new JAXBElement<>(_EndringIRefusjonRefusjonsbeloepPrMnd_QNAME, BigDecimal.class, Refusjon.class, value);
    }

    @XmlElementDecl(
            namespace = NAMESPACE_URI,
            name = "endringIRefusjonListe",
            scope = Refusjon.class
    )
    public JAXBElement<EndringIRefusjonsListe> createRefusjonEndringIRefusjonListe(EndringIRefusjonsListe value) {
        return new JAXBElement<>(_RefusjonEndringIRefusjonListe_QNAME, EndringIRefusjonsListe.class, Refusjon.class, value);
    }
}
