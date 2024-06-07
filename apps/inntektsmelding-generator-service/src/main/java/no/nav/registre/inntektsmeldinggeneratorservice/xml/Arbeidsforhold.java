//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.registre.inntektsmeldinggeneratorservice.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Arbeidsforhold",
        propOrder = { "arbeidsforholdId", "foersteFravaersdag", "beregnetInntekt", "avtaltFerieListe", "utsettelseAvForeldrepengerListe", "graderingIForeldrepengerListe" }
)
public class Arbeidsforhold {
    @XmlElementRef(
            name = "arbeidsforholdId",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<String> arbeidsforholdId;
    @XmlElementRef(
            name = "foersteFravaersdag",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<LocalDate> foersteFravaersdag;
    @XmlElementRef(
            name = "beregnetInntekt",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<Inntekt> beregnetInntekt;
    @XmlElementRef(
            name = "avtaltFerieListe",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<AvtaltFerieListe> avtaltFerieListe;
    @XmlElementRef(
            name = "utsettelseAvForeldrepengerListe",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<UtsettelseAvForeldrepengerListe> utsettelseAvForeldrepengerListe;
    @XmlElementRef(
            name = "graderingIForeldrepengerListe",
            namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
            type = JAXBElement.class
    )
    protected JAXBElement<GraderingIForeldrepengerListe> graderingIForeldrepengerListe;

    public Arbeidsforhold() {
    }

    public Arbeidsforhold(JAXBElement<String> arbeidsforholdId, JAXBElement<LocalDate> foersteFravaersdag, JAXBElement<Inntekt> beregnetInntekt, JAXBElement<AvtaltFerieListe> avtaltFerieListe, JAXBElement<UtsettelseAvForeldrepengerListe> utsettelseAvForeldrepengerListe, JAXBElement<GraderingIForeldrepengerListe> graderingIForeldrepengerListe) {
        this.arbeidsforholdId = arbeidsforholdId;
        this.foersteFravaersdag = foersteFravaersdag;
        this.beregnetInntekt = beregnetInntekt;
        this.avtaltFerieListe = avtaltFerieListe;
        this.utsettelseAvForeldrepengerListe = utsettelseAvForeldrepengerListe;
        this.graderingIForeldrepengerListe = graderingIForeldrepengerListe;
    }

}
