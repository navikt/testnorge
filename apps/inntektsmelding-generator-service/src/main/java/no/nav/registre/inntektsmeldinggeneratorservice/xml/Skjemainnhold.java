//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.registre.inntektsmeldinggeneratorservice.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Skjemainnhold",
        propOrder = { "ytelse", "aarsakTilInnsending", "arbeidsgiver", "arbeidsgiverPrivat", "arbeidstakerFnr", "naerRelasjon", "arbeidsforhold", "refusjon", "sykepengerIArbeidsgiverperioden", "startdatoForeldrepengeperiode", "opphoerAvNaturalytelseListe", "gjenopptakelseNaturalytelseListe", "avsendersystem", "pleiepengerPerioder", "omsorgspenger" }
)
public class Skjemainnhold {
    @Getter
    @XmlElement(
            required = true
    )
    protected String ytelse;
    @Getter
    @XmlElement(
            required = true
    )
    protected String aarsakTilInnsending;
    @Getter
    @XmlElement(
            required = true
    )
    protected JAXBElement<Arbeidsgiver> arbeidsgiver;
    @XmlElement(
            required = true
    )

    protected JAXBElement<ArbeidsgiverPrivat> arbeidsgiverPrivat;
    @Getter
    @XmlElement(
            required = true
    )
    protected String arbeidstakerFnr;
    @Getter
    protected boolean naerRelasjon;
    @Getter
    @XmlElementRef(
            name = "arbeidsforhold",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<Arbeidsforhold> arbeidsforhold;
    @Getter
    @XmlElementRef(
            name = "refusjon",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<Refusjon> refusjon;
    @Getter
    @XmlElementRef(
            name = "sykepengerIArbeidsgiverperioden",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<SykepengerIArbeidsgiverperioden> sykepengerIArbeidsgiverperioden;
    @Getter
    @XmlElementRef(
            name = "startdatoForeldrepengeperiode",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<LocalDate> startdatoForeldrepengeperiode;
    @Getter
    @XmlElementRef(
            name = "opphoerAvNaturalytelseListe",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<OpphoerAvNaturalytelseListe> opphoerAvNaturalytelseListe;
    @Getter
    @XmlElementRef(
            name = "gjenopptakelseNaturalytelseListe",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<GjenopptakelseNaturalytelseListe> gjenopptakelseNaturalytelseListe;
    @Getter
    @XmlElement(
            required = true
    )
    protected Avsendersystem avsendersystem;
    @Getter
    @XmlElementRef(
            name = "pleiepengerPerioder",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<PleiepengerPeriodeListe> pleiepengerPerioder;
    @Getter
    @XmlElementRef(
            name = "omsorgspenger",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<Omsorgspenger> omsorgspenger;

    public Skjemainnhold() {
    }

    public Skjemainnhold(String ytelse, String aarsakTilInnsending, JAXBElement<Arbeidsgiver> arbeidsgiver, JAXBElement<ArbeidsgiverPrivat> arbeidsgiverPrivat, String arbeidstakerFnr, boolean naerRelasjon, JAXBElement<Arbeidsforhold> arbeidsforhold, JAXBElement<Refusjon> refusjon, JAXBElement<SykepengerIArbeidsgiverperioden> sykepengerIArbeidsgiverperioden, JAXBElement<LocalDate> startdatoForeldrepengeperiode, JAXBElement<OpphoerAvNaturalytelseListe> opphoerAvNaturalytelseListe, JAXBElement<GjenopptakelseNaturalytelseListe> gjenopptakelseNaturalytelseListe, Avsendersystem avsendersystem, JAXBElement<PleiepengerPeriodeListe> pleiepengerPerioder, JAXBElement<Omsorgspenger> omsorgspenger) {
        this.ytelse = ytelse;
        this.aarsakTilInnsending = aarsakTilInnsending;
        this.arbeidsgiver = arbeidsgiver;
        this.arbeidsgiverPrivat = arbeidsgiverPrivat;
        this.arbeidstakerFnr = arbeidstakerFnr;
        this.naerRelasjon = naerRelasjon;
        this.arbeidsforhold = arbeidsforhold;
        this.refusjon = refusjon;
        this.sykepengerIArbeidsgiverperioden = sykepengerIArbeidsgiverperioden;
        this.startdatoForeldrepengeperiode = startdatoForeldrepengeperiode;
        this.opphoerAvNaturalytelseListe = opphoerAvNaturalytelseListe;
        this.gjenopptakelseNaturalytelseListe = gjenopptakelseNaturalytelseListe;
        this.avsendersystem = avsendersystem;
        this.pleiepengerPerioder = pleiepengerPerioder;
        this.omsorgspenger = omsorgspenger;
    }

    public <T> Skjemainnhold(String ytelse, String aarsakTilInnsending, JAXBElement<T> arbeidsgiver, String arbeidstakerFnr, boolean naerRelasjon, JAXBElement<T> arbeidsforhold, JAXBElement<T> refusjon, JAXBElement<T> sykepengerIArbeidsgiverPerioden, JAXBElement<T> startdatoForeldrepengerperiode, JAXBElement<T> opphoerAvNaturalyrelseListe, JAXBElement<T> gjenopptakelseNaturalytelseListe, Avsendersystem avsendersystem, JAXBElement<T> pleiepengerPeriodeListe, JAXBElement<T> omsorgspenger) {
    }

    public Skjemainnhold withYtelse(String value) {
        this.setYtelse(value);
        return this;
    }

    public Skjemainnhold withAarsakTilInnsending(String value) {
        this.setAarsakTilInnsending(value);
        return this;
    }

    public Skjemainnhold withArbeidsgiver(JAXBElement<Arbeidsgiver> value) {
        this.setArbeidsgiver(value);
        return this;
    }

    public Skjemainnhold withArbeidstakerFnr(String value) {
        this.setArbeidstakerFnr(value);
        return this;
    }

    public Skjemainnhold withNaerRelasjon(boolean value) {
        this.setNaerRelasjon(value);
        return this;
    }

    public Skjemainnhold withArbeidsforhold(JAXBElement<Arbeidsforhold> value) {
        this.setArbeidsforhold(value);
        return this;
    }

    public Skjemainnhold withRefusjon(JAXBElement<Refusjon> value) {
        this.setRefusjon(value);
        return this;
    }

    public Skjemainnhold withSykepengerIArbeidsgiverperioden(JAXBElement<SykepengerIArbeidsgiverperioden> value) {
        this.setSykepengerIArbeidsgiverperioden(value);
        return this;
    }

    public Skjemainnhold withStartdatoForeldrepengeperiode(JAXBElement<LocalDate> value) {
        this.setStartdatoForeldrepengeperiode(value);
        return this;
    }

    public Skjemainnhold withOpphoerAvNaturalytelseListe(JAXBElement<OpphoerAvNaturalytelseListe> value) {
        this.setOpphoerAvNaturalytelseListe(value);
        return this;
    }

    public Skjemainnhold withGjenopptakelseNaturalytelseListe(JAXBElement<GjenopptakelseNaturalytelseListe> value) {
        this.setGjenopptakelseNaturalytelseListe(value);
        return this;
    }

    public Skjemainnhold withAvsendersystem(Avsendersystem value) {
        this.setAvsendersystem(value);
        return this;
    }

    public Skjemainnhold withPleiepengerPerioder(JAXBElement<PleiepengerPeriodeListe> value) {
        this.setPleiepengerPerioder(value);
        return this;
    }

    public Skjemainnhold withOmsorgspenger(JAXBElement<Omsorgspenger> value) {
        this.setOmsorgspenger(value);
        return this;
    }
}
