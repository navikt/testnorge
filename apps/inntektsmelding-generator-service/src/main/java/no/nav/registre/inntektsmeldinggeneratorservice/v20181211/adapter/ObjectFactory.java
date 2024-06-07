//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.adapter;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Arbeidsforhold;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Arbeidsgiver;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.ArbeidsgiverPrivat;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.ArbeidsgiverperiodeListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Avsendersystem;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.AvtaltFerieListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.DelvisFravaer;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.DelvisFravaersListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.EndringIRefusjon;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.EndringIRefusjonsListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.FravaersPeriodeListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.GjenopptakelseNaturalytelseListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.GraderingIForeldrepenger;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.GraderingIForeldrepengerListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Inntekt;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.InntektsmeldingM;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Kontaktinformasjon;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.NaturalytelseDetaljer;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Omsorgspenger;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.OpphoerAvNaturalytelseListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Periode;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.PleiepengerPeriodeListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Refusjon;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Skjemainnhold;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.SykepengerIArbeidsgiverperioden;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.UtsettelseAvForeldrepenger;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.UtsettelseAvForeldrepengerListe;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@XmlRegistry
public class ObjectFactory {
    private static final QName _Melding_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "melding");
    private static final QName _EndringIRefusjonRefusjonsbeloepPrMnd_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "refusjonsbeloepPrMnd");
    private static final QName _EndringIRefusjonEndringsdato_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "endringsdato");
    private static final QName _UtsettelseAvForeldrepengerPeriode_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "periode");
    private static final QName _UtsettelseAvForeldrepengerAarsakTilUtsettelse_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "aarsakTilUtsettelse");
    private static final QName _AvsendersystemInnsendingstidspunkt_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "innsendingstidspunkt");
    private static final QName _SkjemainnholdArbeidsforhold_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "arbeidsforhold");
    private static final QName _SkjemainnholdRefusjon_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "refusjon");
    private static final QName _SkjemainnholdSykepengerIArbeidsgiverperioden_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "sykepengerIArbeidsgiverperioden");
    private static final QName _SkjemainnholdGjenopptakelseNaturalytelseListe_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "gjenopptakelseNaturalytelseListe");
    private static final QName _SkjemainnholdOmsorgspenger_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "omsorgspenger");
    private static final QName _SkjemainnholdArbeidsgiver_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "arbeidsgiver");
    private static final QName _SkjemainnholdStartdatoForeldrepengeperiode_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "startdatoForeldrepengeperiode");
    private static final QName _SkjemainnholdOpphoerAvNaturalytelseListe_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "opphoerAvNaturalytelseListe");
    private static final QName _SkjemainnholdPleiepengerPerioder_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "pleiepengerPerioder");
    private static final QName _SkjemainnholdArbeidsgiverPrivat_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "arbeidsgiverPrivat");
    private static final QName _SykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "arbeidsgiverperiodeListe");
    private static final QName _SykepengerIArbeidsgiverperiodenBruttoUtbetalt_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "bruttoUtbetalt");
    private static final QName _SykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "begrunnelseForReduksjonEllerIkkeUtbetalt");
    private static final QName _OmsorgspengerFravaersPerioder_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "fravaersPerioder");
    private static final QName _OmsorgspengerHarUtbetaltPliktigeDager_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "harUtbetaltPliktigeDager");
    private static final QName _OmsorgspengerDelvisFravaersListe_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "delvisFravaersListe");
    private static final QName _GraderingIForeldrepengerArbeidstidprosent_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "arbeidstidprosent");
    private static final QName _DelvisFravaerDato_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "dato");
    private static final QName _DelvisFravaerTimer_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "timer");
    private static final QName _PeriodeFom_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "fom");
    private static final QName _PeriodeTom_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "tom");
    private static final QName _InntektBeloep_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "beloep");
    private static final QName _InntektAarsakVedEndring_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "aarsakVedEndring");
    private static final QName _ArbeidsforholdFoersteFravaersdag_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "foersteFravaersdag");
    private static final QName _ArbeidsforholdBeregnetInntekt_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "beregnetInntekt");
    private static final QName _ArbeidsforholdArbeidsforholdId_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "arbeidsforholdId");
    private static final QName _ArbeidsforholdGraderingIForeldrepengerListe_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "graderingIForeldrepengerListe");
    private static final QName _ArbeidsforholdUtsettelseAvForeldrepengerListe_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "utsettelseAvForeldrepengerListe");
    private static final QName _ArbeidsforholdAvtaltFerieListe_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "avtaltFerieListe");
    private static final QName _NaturalytelseDetaljerBeloepPrMnd_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "beloepPrMnd");
    private static final QName _NaturalytelseDetaljerNaturalytelseType_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "naturalytelseType");
    private static final QName _RefusjonRefusjonsopphoersdato_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "refusjonsopphoersdato");
    private static final QName _RefusjonEndringIRefusjonListe_QNAME = new QName("http://seres.no/xsd/NAV/Inntektsmelding_M/20181211", "endringIRefusjonListe");

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
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "melding"
    )
    public JAXBElement<InntektsmeldingM> createMelding(InntektsmeldingM value) {
        return new JAXBElement<>(_Melding_QNAME, InntektsmeldingM.class, null, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "refusjonsbeloepPrMnd",
            scope = EndringIRefusjon.class
    )
    public JAXBElement createEndringIRefusjonRefusjonsbeloepPrMnd(BigDecimal value) {
        return new JAXBElement<>(_EndringIRefusjonRefusjonsbeloepPrMnd_QNAME, BigDecimal.class, EndringIRefusjon.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "endringsdato",
            scope = EndringIRefusjon.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createEndringIRefusjonEndringsdato(LocalDate value) {
        return new JAXBElement<>(_EndringIRefusjonEndringsdato_QNAME, LocalDate.class, EndringIRefusjon.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "periode",
            scope = UtsettelseAvForeldrepenger.class
    )
    public JAXBElement createUtsettelseAvForeldrepengerPeriode(Periode value) {
        return new JAXBElement<>(_UtsettelseAvForeldrepengerPeriode_QNAME, Periode.class, UtsettelseAvForeldrepenger.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "aarsakTilUtsettelse",
            scope = UtsettelseAvForeldrepenger.class
    )
    public JAXBElement createUtsettelseAvForeldrepengerAarsakTilUtsettelse(String value) {
        return new JAXBElement<>(_UtsettelseAvForeldrepengerAarsakTilUtsettelse_QNAME, String.class, UtsettelseAvForeldrepenger.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "innsendingstidspunkt",
            scope = Avsendersystem.class
    )
    @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter.class)
    public JAXBElement createAvsendersystemInnsendingstidspunkt(LocalDateTime value) {
        return new JAXBElement<>(_AvsendersystemInnsendingstidspunkt_QNAME, LocalDateTime.class, Avsendersystem.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "arbeidsforhold",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdArbeidsforhold(Arbeidsforhold value) {
        return new JAXBElement<>(_SkjemainnholdArbeidsforhold_QNAME, Arbeidsforhold.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "refusjon",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdRefusjon(Refusjon value) {
        return new JAXBElement<>(_SkjemainnholdRefusjon_QNAME, Refusjon.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "sykepengerIArbeidsgiverperioden",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdSykepengerIArbeidsgiverperioden(SykepengerIArbeidsgiverperioden value) {
        return new JAXBElement<>(_SkjemainnholdSykepengerIArbeidsgiverperioden_QNAME, SykepengerIArbeidsgiverperioden.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "gjenopptakelseNaturalytelseListe",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdGjenopptakelseNaturalytelseListe(GjenopptakelseNaturalytelseListe value) {
        return new JAXBElement<>(_SkjemainnholdGjenopptakelseNaturalytelseListe_QNAME, GjenopptakelseNaturalytelseListe.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "omsorgspenger",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdOmsorgspenger(Omsorgspenger value) {
        return new JAXBElement<>(_SkjemainnholdOmsorgspenger_QNAME, Omsorgspenger.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "arbeidsgiver",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdArbeidsgiver(Arbeidsgiver value) {
        return new JAXBElement<>(_SkjemainnholdArbeidsgiver_QNAME, Arbeidsgiver.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "startdatoForeldrepengeperiode",
            scope = Skjemainnhold.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createSkjemainnholdStartdatoForeldrepengeperiode(LocalDate value) {
        return new JAXBElement<>(_SkjemainnholdStartdatoForeldrepengeperiode_QNAME, LocalDate.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "opphoerAvNaturalytelseListe",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdOpphoerAvNaturalytelseListe(OpphoerAvNaturalytelseListe value) {
        return new JAXBElement<>(_SkjemainnholdOpphoerAvNaturalytelseListe_QNAME, OpphoerAvNaturalytelseListe.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "pleiepengerPerioder",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdPleiepengerPerioder(PleiepengerPeriodeListe value) {
        return new JAXBElement<>(_SkjemainnholdPleiepengerPerioder_QNAME, PleiepengerPeriodeListe.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "arbeidsgiverPrivat",
            scope = Skjemainnhold.class
    )
    public JAXBElement createSkjemainnholdArbeidsgiverPrivat(ArbeidsgiverPrivat value) {
        return new JAXBElement<>(_SkjemainnholdArbeidsgiverPrivat_QNAME, ArbeidsgiverPrivat.class, Skjemainnhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "arbeidsgiverperiodeListe",
            scope = SykepengerIArbeidsgiverperioden.class
    )
    public JAXBElement createSykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe(ArbeidsgiverperiodeListe value) {
        return new JAXBElement<>(_SykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe_QNAME, ArbeidsgiverperiodeListe.class, SykepengerIArbeidsgiverperioden.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "bruttoUtbetalt",
            scope = SykepengerIArbeidsgiverperioden.class
    )
    public JAXBElement createSykepengerIArbeidsgiverperiodenBruttoUtbetalt(BigDecimal value) {
        return new JAXBElement<>(_SykepengerIArbeidsgiverperiodenBruttoUtbetalt_QNAME, BigDecimal.class, SykepengerIArbeidsgiverperioden.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "begrunnelseForReduksjonEllerIkkeUtbetalt",
            scope = SykepengerIArbeidsgiverperioden.class
    )
    public JAXBElement createSykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt(String value) {
        return new JAXBElement<>(_SykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt_QNAME, String.class, SykepengerIArbeidsgiverperioden.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "fravaersPerioder",
            scope = Omsorgspenger.class
    )
    public JAXBElement createOmsorgspengerFravaersPerioder(FravaersPeriodeListe value) {
        return new JAXBElement<>(_OmsorgspengerFravaersPerioder_QNAME, FravaersPeriodeListe.class, Omsorgspenger.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "harUtbetaltPliktigeDager",
            scope = Omsorgspenger.class
    )
    public JAXBElement createOmsorgspengerHarUtbetaltPliktigeDager(Boolean value) {
        return new JAXBElement<>(_OmsorgspengerHarUtbetaltPliktigeDager_QNAME, Boolean.class, Omsorgspenger.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "delvisFravaersListe",
            scope = Omsorgspenger.class
    )
    public JAXBElement createOmsorgspengerDelvisFravaersListe(DelvisFravaersListe value) {
        return new JAXBElement<>(_OmsorgspengerDelvisFravaersListe_QNAME, DelvisFravaersListe.class, Omsorgspenger.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "periode",
            scope = GraderingIForeldrepenger.class
    )
    public JAXBElement createGraderingIForeldrepengerPeriode(Periode value) {
        return new JAXBElement<>(_UtsettelseAvForeldrepengerPeriode_QNAME, Periode.class, GraderingIForeldrepenger.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "arbeidstidprosent",
            scope = GraderingIForeldrepenger.class
    )
    public JAXBElement createGraderingIForeldrepengerArbeidstidprosent(BigInteger value) {
        return new JAXBElement<>(_GraderingIForeldrepengerArbeidstidprosent_QNAME, BigInteger.class, GraderingIForeldrepenger.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "dato",
            scope = DelvisFravaer.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createDelvisFravaerDato(LocalDate value) {
        return new JAXBElement<>(_DelvisFravaerDato_QNAME, LocalDate.class, DelvisFravaer.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "timer",
            scope = DelvisFravaer.class
    )
    public JAXBElement createDelvisFravaerTimer(BigDecimal value) {
        return new JAXBElement<>(_DelvisFravaerTimer_QNAME, BigDecimal.class, DelvisFravaer.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "fom",
            scope = Periode.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createPeriodeFom(LocalDate value) {
        return new JAXBElement<>(_PeriodeFom_QNAME, LocalDate.class, Periode.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "tom",
            scope = Periode.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createPeriodeTom(LocalDate value) {
        return new JAXBElement<>(_PeriodeTom_QNAME, LocalDate.class, Periode.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "beloep",
            scope = Inntekt.class
    )
    public JAXBElement createInntektBeloep(BigDecimal value) {
        return new JAXBElement<>(_InntektBeloep_QNAME, BigDecimal.class, Inntekt.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "aarsakVedEndring",
            scope = Inntekt.class
    )
    public JAXBElement createInntektAarsakVedEndring(String value) {
        return new JAXBElement<>(_InntektAarsakVedEndring_QNAME, String.class, Inntekt.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "foersteFravaersdag",
            scope = Arbeidsforhold.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createArbeidsforholdFoersteFravaersdag(LocalDate value) {
        return new JAXBElement<>(_ArbeidsforholdFoersteFravaersdag_QNAME, LocalDate.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "beregnetInntekt",
            scope = Arbeidsforhold.class
    )
    public JAXBElement createArbeidsforholdBeregnetInntekt(Inntekt value) {
        return new JAXBElement<>(_ArbeidsforholdBeregnetInntekt_QNAME, Inntekt.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "arbeidsforholdId",
            scope = Arbeidsforhold.class
    )
    public JAXBElement createArbeidsforholdArbeidsforholdId(String value) {
        return new JAXBElement<>(_ArbeidsforholdArbeidsforholdId_QNAME, String.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "graderingIForeldrepengerListe",
            scope = Arbeidsforhold.class
    )
    public JAXBElement createArbeidsforholdGraderingIForeldrepengerListe(GraderingIForeldrepengerListe value) {
        return new JAXBElement<>(_ArbeidsforholdGraderingIForeldrepengerListe_QNAME, GraderingIForeldrepengerListe.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "utsettelseAvForeldrepengerListe",
            scope = Arbeidsforhold.class
    )
    public JAXBElement createArbeidsforholdUtsettelseAvForeldrepengerListe(UtsettelseAvForeldrepengerListe value) {
        return new JAXBElement<>(_ArbeidsforholdUtsettelseAvForeldrepengerListe_QNAME, UtsettelseAvForeldrepengerListe.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "avtaltFerieListe",
            scope = Arbeidsforhold.class
    )
    public JAXBElement createArbeidsforholdAvtaltFerieListe(AvtaltFerieListe value) {
        return new JAXBElement<>(_ArbeidsforholdAvtaltFerieListe_QNAME, AvtaltFerieListe.class, Arbeidsforhold.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "beloepPrMnd",
            scope = NaturalytelseDetaljer.class
    )
    public JAXBElement createNaturalytelseDetaljerBeloepPrMnd(BigDecimal value) {
        return new JAXBElement<>(_NaturalytelseDetaljerBeloepPrMnd_QNAME, BigDecimal.class, NaturalytelseDetaljer.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "fom",
            scope = NaturalytelseDetaljer.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createNaturalytelseDetaljerFom(LocalDate value) {
        return new JAXBElement<>(_PeriodeFom_QNAME, LocalDate.class, NaturalytelseDetaljer.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "naturalytelseType",
            scope = NaturalytelseDetaljer.class
    )
    public JAXBElement createNaturalytelseDetaljerNaturalytelseType(String value) {
        return new JAXBElement<>(_NaturalytelseDetaljerNaturalytelseType_QNAME, String.class, NaturalytelseDetaljer.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "refusjonsopphoersdato",
            scope = Refusjon.class
    )
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public JAXBElement createRefusjonRefusjonsopphoersdato(LocalDate value) {
        return new JAXBElement<>(_RefusjonRefusjonsopphoersdato_QNAME, LocalDate.class, Refusjon.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "refusjonsbeloepPrMnd",
            scope = Refusjon.class
    )
    public JAXBElement createRefusjonRefusjonsbeloepPrMnd(BigDecimal value) {
        return new JAXBElement<>(_EndringIRefusjonRefusjonsbeloepPrMnd_QNAME, BigDecimal.class, Refusjon.class, value);
    }

    @XmlElementDecl(
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            name = "endringIRefusjonListe",
            scope = Refusjon.class
    )
    public JAXBElement createRefusjonEndringIRefusjonListe(EndringIRefusjonsListe value) {
        return new JAXBElement<>(_RefusjonEndringIRefusjonListe_QNAME, EndringIRefusjonsListe.class, Refusjon.class, value);
    }
}
