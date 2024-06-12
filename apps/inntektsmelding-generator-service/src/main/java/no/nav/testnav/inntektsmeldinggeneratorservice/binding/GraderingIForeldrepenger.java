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

import java.math.BigInteger;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "GraderingIForeldrepenger",
    propOrder = {"periode", "arbeidstidprosent"}
)
public class GraderingIForeldrepenger {
    @XmlElementRef(
        name = "periode",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<Periode> periode;
    @XmlElementRef(
        name = "arbeidstidprosent",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<BigInteger> arbeidstidprosent;

    public GraderingIForeldrepenger() {
    }

    public GraderingIForeldrepenger(JAXBElement<Periode> periode, JAXBElement<BigInteger> arbeidstidprosent) {
        this.periode = periode;
        this.arbeidstidprosent = arbeidstidprosent;
    }

    public GraderingIForeldrepenger withPeriode(JAXBElement<Periode> value) {
        this.setPeriode(value);
        return this;
    }

    public GraderingIForeldrepenger withArbeidstidprosent(JAXBElement<BigInteger> value) {
        this.setArbeidstidprosent(value);
        return this;
    }
}
