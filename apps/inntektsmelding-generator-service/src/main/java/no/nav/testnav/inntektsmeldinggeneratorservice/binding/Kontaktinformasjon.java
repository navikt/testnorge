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
    name = "Kontaktinformasjon",
    propOrder = {"kontaktinformasjonNavn", "telefonnummer"}
)
public class Kontaktinformasjon {
    @XmlElement(
        required = true
    )
    protected String kontaktinformasjonNavn;
    @XmlElement(
        required = true
    )
    protected String telefonnummer;

    public Kontaktinformasjon() {
    }

    public Kontaktinformasjon(String kontaktinformasjonNavn, String telefonnummer) {
        this.kontaktinformasjonNavn = kontaktinformasjonNavn;
        this.telefonnummer = telefonnummer;
    }

    public Kontaktinformasjon withKontaktinformasjonNavn(String value) {
        this.setKontaktinformasjonNavn(value);
        return this;
    }

    public Kontaktinformasjon withTelefonnummer(String value) {
        this.setTelefonnummer(value);
        return this;
    }
}
