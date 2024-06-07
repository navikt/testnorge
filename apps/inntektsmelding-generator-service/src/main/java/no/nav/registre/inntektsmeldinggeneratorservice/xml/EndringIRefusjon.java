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
    name = "EndringIRefusjon",
    propOrder = {"endringsdato", "refusjonsbeloepPrMnd"}
)
public class EndringIRefusjon {
    @XmlElementRef(
        name = "endringsdato",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<LocalDate> endringsdato;
    @XmlElementRef(
        name = "refusjonsbeloepPrMnd",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<BigDecimal> refusjonsbeloepPrMnd;

    public EndringIRefusjon() {
    }

    public EndringIRefusjon(JAXBElement<LocalDate> endringsdato, JAXBElement<BigDecimal> refusjonsbeloepPrMnd) {
        this.endringsdato = endringsdato;
        this.refusjonsbeloepPrMnd = refusjonsbeloepPrMnd;
    }

    public EndringIRefusjon withEndringsdato(JAXBElement<LocalDate> value) {
        this.setEndringsdato(value);
        return this;
    }

    public EndringIRefusjon withRefusjonsbeloepPrMnd(JAXBElement<BigDecimal> value) {
        this.setRefusjonsbeloepPrMnd(value);
        return this;
    }
}
