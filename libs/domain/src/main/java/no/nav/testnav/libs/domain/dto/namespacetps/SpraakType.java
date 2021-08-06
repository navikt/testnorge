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
        name = "spraakType",
        propOrder = { "spraak", "spraakFraDato", "spraakTilDato", "spraakKilde", "spraakRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SpraakType {

    @XmlElement(
            required = true
    )
    protected String spraak;
    @XmlElement(
            required = true
    )
    protected String spraakFraDato;
    @XmlElement(
            required = true
    )
    protected String spraakTilDato;
    @XmlElement(
            required = true
    )
    protected String spraakKilde;
    @XmlElement(
            required = true
    )
    protected String spraakRegistrertAv;

    public SpraakType withSpraak(String value) {
        this.setSpraak(value);
        return this;
    }

    public SpraakType withSpraakFraDato(String value) {
        this.setSpraakFraDato(value);
        return this;
    }

    public SpraakType withSpraakTilDato(String value) {
        this.setSpraakTilDato(value);
        return this;
    }

    public SpraakType withSpraakKilde(String value) {
        this.setSpraakKilde(value);
        return this;
    }

    public SpraakType withSpraakRegistrertAv(String value) {
        this.setSpraakRegistrertAv(value);
        return this;
    }
}
