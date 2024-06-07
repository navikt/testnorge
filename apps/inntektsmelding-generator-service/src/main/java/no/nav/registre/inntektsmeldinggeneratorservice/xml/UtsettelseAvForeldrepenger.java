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

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "UtsettelseAvForeldrepenger",
    propOrder = {"periode", "aarsakTilUtsettelse"}
)
public class UtsettelseAvForeldrepenger {
    @XmlElementRef(
        name = "periode",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<Periode> periode;
    @XmlElementRef(
        name = "aarsakTilUtsettelse",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<String> aarsakTilUtsettelse;

    public UtsettelseAvForeldrepenger() {
    }

    public UtsettelseAvForeldrepenger(JAXBElement<Periode> periode, JAXBElement<String> aarsakTilUtsettelse) {
        this.periode = periode;
        this.aarsakTilUtsettelse = aarsakTilUtsettelse;
    }

    public UtsettelseAvForeldrepenger withPeriode(JAXBElement<Periode> value) {
        this.setPeriode(value);
        return this;
    }

    public UtsettelseAvForeldrepenger withAarsakTilUtsettelse(JAXBElement<String> value) {
        this.setAarsakTilUtsettelse(value);
        return this;
    }
}
