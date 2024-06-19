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

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "Omsorgspenger",
    propOrder = {"harUtbetaltPliktigeDager", "fravaersPerioder", "delvisFravaersListe"}
)
public class Omsorgspenger {
    @XmlElementRef(
        name = "harUtbetaltPliktigeDager",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<Boolean> harUtbetaltPliktigeDager;
    @XmlElementRef(
        name = "fravaersPerioder",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<FravaersPeriodeListe> fravaersPerioder;
    @XmlElementRef(
        name = "delvisFravaersListe",
        namespace = "http://seres.no/xsd/NAV/Inntektsmelding_M/20181211",
        type = JAXBElement.class
    )
    protected JAXBElement<DelvisFravaersListe> delvisFravaersListe;

    public Omsorgspenger() {
    }

    public Omsorgspenger(JAXBElement<Boolean> harUtbetaltPliktigeDager, JAXBElement<FravaersPeriodeListe> fravaersPerioder, JAXBElement<DelvisFravaersListe> delvisFravaersListe) {
        this.harUtbetaltPliktigeDager = harUtbetaltPliktigeDager;
        this.fravaersPerioder = fravaersPerioder;
        this.delvisFravaersListe = delvisFravaersListe;
    }

    public Omsorgspenger withHarUtbetaltPliktigeDager(JAXBElement<Boolean> value) {
        this.setHarUtbetaltPliktigeDager(value);
        return this;
    }

    public Omsorgspenger withFravaersPerioder(JAXBElement<FravaersPeriodeListe> value) {
        this.setFravaersPerioder(value);
        return this;
    }

    public Omsorgspenger withDelvisFravaersListe(JAXBElement<DelvisFravaersListe> value) {
        this.setDelvisFravaersListe(value);
        return this;
    }
}
