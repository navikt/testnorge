//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.testnav.inntektsmeldinggeneratorservice.binding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ArbeidsgiverPrivat",
        propOrder = { "arbeidsgiverFnr", "kontaktinformasjon" }
)
public class ArbeidsgiverPrivat {
    @XmlElement(
            required = true
    )
    protected String arbeidsgiverFnr;
    @XmlElement(
            required = true
    )
    protected Kontaktinformasjon kontaktinformasjon;

    public ArbeidsgiverPrivat() {
    }

    public ArbeidsgiverPrivat(String arbeidsgiverFnr, Kontaktinformasjon kontaktinformasjon) {
        this.arbeidsgiverFnr = arbeidsgiverFnr;
        this.kontaktinformasjon = kontaktinformasjon;
    }

    public ArbeidsgiverPrivat withArbeidsgiverFnr(String value) {
        this.setArbeidsgiverFnr(value);
        return this;
    }

    public ArbeidsgiverPrivat withKontaktinformasjon(Kontaktinformasjon value) {
        this.setKontaktinformasjon(value);
        return this;
    }
}
