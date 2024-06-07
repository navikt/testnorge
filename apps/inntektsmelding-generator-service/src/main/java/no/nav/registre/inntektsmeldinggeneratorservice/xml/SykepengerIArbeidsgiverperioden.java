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
    name = "SykepengerIArbeidsgiverperioden",
    propOrder = {"arbeidsgiverperiodeListe", "bruttoUtbetalt", "begrunnelseForReduksjonEllerIkkeUtbetalt"}
)
public class SykepengerIArbeidsgiverperioden {
    @XmlElementRef(
        name = "arbeidsgiverperiodeListe",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<ArbeidsgiverperiodeListe> arbeidsgiverperiodeListe;
    @XmlElementRef(
        name = "bruttoUtbetalt",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<BigDecimal> bruttoUtbetalt;
    @XmlElementRef(
        name = "begrunnelseForReduksjonEllerIkkeUtbetalt",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<String> begrunnelseForReduksjonEllerIkkeUtbetalt;

    public SykepengerIArbeidsgiverperioden() {
    }

    public SykepengerIArbeidsgiverperioden(JAXBElement<ArbeidsgiverperiodeListe> arbeidsgiverperiodeListe, JAXBElement<BigDecimal> bruttoUtbetalt, JAXBElement<String> begrunnelseForReduksjonEllerIkkeUtbetalt) {
        this.arbeidsgiverperiodeListe = arbeidsgiverperiodeListe;
        this.bruttoUtbetalt = bruttoUtbetalt;
        this.begrunnelseForReduksjonEllerIkkeUtbetalt = begrunnelseForReduksjonEllerIkkeUtbetalt;
    }

    public SykepengerIArbeidsgiverperioden withArbeidsgiverperiodeListe(JAXBElement<ArbeidsgiverperiodeListe> value) {
        this.setArbeidsgiverperiodeListe(value);
        return this;
    }

    public SykepengerIArbeidsgiverperioden withBruttoUtbetalt(JAXBElement<BigDecimal> value) {
        this.setBruttoUtbetalt(value);
        return this;
    }

    public SykepengerIArbeidsgiverperioden withBegrunnelseForReduksjonEllerIkkeUtbetalt(JAXBElement<String> value) {
        this.setBegrunnelseForReduksjonEllerIkkeUtbetalt(value);
        return this;
    }
}
