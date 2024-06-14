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

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "Refusjon",
    propOrder = {"refusjonsbeloepPrMnd", "refusjonsopphoersdato", "endringIRefusjonListe"}
)
public class Refusjon {
    @XmlElementRef(
        name = "refusjonsbeloepPrMnd",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<BigDecimal> refusjonsbeloepPrMnd;
    @XmlElementRef(
        name = "refusjonsopphoersdato",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<LocalDate> refusjonsopphoersdato;
    @XmlElementRef(
        name = "endringIRefusjonListe",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<EndringIRefusjonsListe> endringIRefusjonListe;

    public Refusjon() {
    }

    public Refusjon(JAXBElement<BigDecimal> refusjonsbeloepPrMnd, JAXBElement<LocalDate> refusjonsopphoersdato, JAXBElement<EndringIRefusjonsListe> endringIRefusjonListe) {
        this.refusjonsbeloepPrMnd = refusjonsbeloepPrMnd;
        this.refusjonsopphoersdato = refusjonsopphoersdato;
        this.endringIRefusjonListe = endringIRefusjonListe;
    }

    public Refusjon withRefusjonsbeloepPrMnd(JAXBElement<BigDecimal> value) {
        this.setRefusjonsbeloepPrMnd(value);
        return this;
    }

    public Refusjon withRefusjonsopphoersdato(JAXBElement<LocalDate> value) {
        this.setRefusjonsopphoersdato(value);
        return this;
    }

    public Refusjon withEndringIRefusjonListe(JAXBElement<EndringIRefusjonsListe> value) {
        this.setEndringIRefusjonListe(value);
        return this;
    }
}
