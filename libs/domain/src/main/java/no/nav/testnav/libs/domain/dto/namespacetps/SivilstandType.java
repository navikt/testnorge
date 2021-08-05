package no.nav.testnav.libs.domain.dto.namespacetps;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "sivilstandType",
        propOrder = { "sivilstand", "sivilstandFraDato", "sivilstandTilDato", "sivilstandKilde", "sivilstandRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SivilstandType {

    @XmlElement(
            required = true
    )
    protected String sivilstand;
    @XmlElement(
            required = true
    )
    protected String sivilstandFraDato;
    @XmlElement(
            required = true
    )
    protected String sivilstandTilDato;
    @XmlElement(
            required = true
    )
    protected String sivilstandKilde;
    @XmlElement(
            required = true
    )
    protected String sivilstandRegistrertAv;

    public SivilstandType withSivilstand(String value) {
        this.setSivilstand(value);
        return this;
    }

    public SivilstandType withSivilstandFraDato(String value) {
        this.setSivilstandFraDato(value);
        return this;
    }

    public SivilstandType withSivilstandTilDato(String value) {
        this.setSivilstandTilDato(value);
        return this;
    }

    public SivilstandType withSivilstandKilde(String value) {
        this.setSivilstandKilde(value);
        return this;
    }

    public SivilstandType withSivilstandRegistrertAv(String value) {
        this.setSivilstandRegistrertAv(value);
        return this;
    }
}
