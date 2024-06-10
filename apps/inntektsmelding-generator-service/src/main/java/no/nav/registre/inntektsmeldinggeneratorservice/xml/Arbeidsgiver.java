//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.registre.inntektsmeldinggeneratorservice.xml;

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
        name = "arbeidsgiver",
        propOrder = { "virksomhetsnummer", "kontaktinformasjon" }
)
public class Arbeidsgiver {
    @XmlElement(
            required = true

    )
    protected String virksomhetsnummer;
    @XmlElement(
            required = true
    )
    protected Kontaktinformasjon kontaktinformasjon;

    public Arbeidsgiver() {
    }

    public Arbeidsgiver(String virksomhetsnummer, Kontaktinformasjon kontaktinformasjon) {
        this.virksomhetsnummer = virksomhetsnummer;
        this.kontaktinformasjon = kontaktinformasjon;
    }

    public Arbeidsgiver withVirksomhetsnummer(String value) {
        this.setVirksomhetsnummer(value);
        return this;
    }

    public Arbeidsgiver withKontaktinformasjon(Kontaktinformasjon value) {
        this.setKontaktinformasjon(value);
        return this;
    }
}
