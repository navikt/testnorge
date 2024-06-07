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
import java.time.LocalDate;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "DelvisFravaer",
    propOrder = {"dato", "timer"}
)
public class DelvisFravaer {
    @XmlElementRef(
        name = "dato",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<LocalDate> dato;
    @XmlElementRef(
        name = "timer",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<BigDecimal> timer;

    public DelvisFravaer() {
    }

    public DelvisFravaer(JAXBElement<LocalDate> dato, JAXBElement<BigDecimal> timer) {
        this.dato = dato;
        this.timer = timer;
    }

    public DelvisFravaer withDato(JAXBElement<LocalDate> value) {
        this.setDato(value);
        return this;
    }

    public DelvisFravaer withTimer(JAXBElement<BigDecimal> value) {
        this.setTimer(value);
        return this;
    }
}
