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
    name = "NaturalytelseDetaljer",
    propOrder = {"naturalytelseType", "fom", "beloepPrMnd"}
)
public class NaturalytelseDetaljer {
    @XmlElementRef(
        name = "naturalytelseType",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<String> naturalytelseType;
    @XmlElementRef(
        name = "fom",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<LocalDate> fom;
    @XmlElementRef(
        name = "beloepPrMnd",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<BigDecimal> beloepPrMnd;

    public NaturalytelseDetaljer() {
    }

    public NaturalytelseDetaljer(JAXBElement<String> naturalytelseType, JAXBElement<LocalDate> fom, JAXBElement<BigDecimal> beloepPrMnd) {
        this.naturalytelseType = naturalytelseType;
        this.fom = fom;
        this.beloepPrMnd = beloepPrMnd;
    }

    public NaturalytelseDetaljer withNaturalytelseType(JAXBElement<String> value) {
        this.setNaturalytelseType(value);
        return this;
    }

    public NaturalytelseDetaljer withFom(JAXBElement<LocalDate> value) {
        this.setFom(value);
        return this;
    }

    public NaturalytelseDetaljer withBeloepPrMnd(JAXBElement<BigDecimal> value) {
        this.setBeloepPrMnd(value);
        return this;
    }
}
