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

import java.time.LocalDateTime;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "Avsendersystem",
    propOrder = {"systemnavn", "systemversjon", "innsendingstidspunkt"}
)
public class Avsendersystem {
    @XmlElement(
        required = true
    )
    protected String systemnavn;
    @XmlElement(
        required = true
    )
    protected String systemversjon;
    @XmlElementRef(
        name = "innsendingstidspunkt",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<LocalDateTime> innsendingstidspunkt;

    public Avsendersystem() {
    }

    public Avsendersystem(String systemnavn, String systemversjon, JAXBElement<LocalDateTime> innsendingstidspunkt) {
        this.systemnavn = systemnavn;
        this.systemversjon = systemversjon;
        this.innsendingstidspunkt = innsendingstidspunkt;
    }

    public Avsendersystem withSystemnavn(String value) {
        this.setSystemnavn(value);
        return this;
    }

    public Avsendersystem withSystemversjon(String value) {
        this.setSystemversjon(value);
        return this;
    }

    public Avsendersystem withInnsendingstidspunkt(JAXBElement<LocalDateTime> value) {
        this.setInnsendingstidspunkt(value);
        return this;
    }
}
