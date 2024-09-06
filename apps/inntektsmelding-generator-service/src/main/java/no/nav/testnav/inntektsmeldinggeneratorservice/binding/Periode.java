//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.testnav.inntektsmeldinggeneratorservice.binding;

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
    name = "Periode",
    propOrder = {"fom", "tom"}
)
public class Periode {
    @XmlElementRef(
        name = "fom",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<LocalDate> fom;
    @XmlElementRef(
        name = "tom",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<LocalDate> tom;

    public Periode() {
    }

    public Periode(JAXBElement<LocalDate> fom, JAXBElement<LocalDate> tom) {
        this.fom = fom;
        this.tom = tom;
    }

    public Periode withFom(JAXBElement<LocalDate> value) {
        this.setFom(value);
        return this;
    }

    public Periode withTom(JAXBElement<LocalDate> value) {
        this.setTom(value);
        return this;
    }
}
