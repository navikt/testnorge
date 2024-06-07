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

import java.math.BigDecimal;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "Inntekt",
    propOrder = {"beloep", "aarsakVedEndring"}
)
public class Inntekt {
    @XmlElementRef(
        name = "beloep",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<BigDecimal> beloep;
    @XmlElementRef(
        name = "aarsakVedEndring",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<String> aarsakVedEndring;

    public Inntekt() {
    }

    public Inntekt(JAXBElement<BigDecimal> beloep, JAXBElement<String> aarsakVedEndring) {
        this.beloep = beloep;
        this.aarsakVedEndring = aarsakVedEndring;
    }

    public Inntekt withBeloep(JAXBElement<BigDecimal> value) {
        this.setBeloep(value);
        return this;
    }

    public Inntekt withAarsakVedEndring(JAXBElement<String> value) {
        this.setAarsakVedEndring(value);
        return this;
    }
}
