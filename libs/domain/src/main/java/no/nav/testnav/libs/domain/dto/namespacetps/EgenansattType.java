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
        name = "egenansattType",
        propOrder = { "egenansatt", "egenansattFraDato", "egenansattTilDato", "egenansattKilde", "egenansattRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EgenansattType {

    @XmlElement(
            required = true
    )
    protected String egenansatt;
    @XmlElement(
            required = true
    )
    protected String egenansattFraDato;
    @XmlElement(
            required = true
    )
    protected String egenansattTilDato;
    @XmlElement(
            required = true
    )
    protected String egenansattKilde;
    @XmlElement(
            required = true
    )
    protected String egenansattRegistrertAv;

    public EgenansattType withEgenansatt(String value) {
        this.setEgenansatt(value);
        return this;
    }

    public EgenansattType withEgenansattFraDato(String value) {
        this.setEgenansattFraDato(value);
        return this;
    }

    public EgenansattType withEgenansattTilDato(String value) {
        this.setEgenansattTilDato(value);
        return this;
    }

    public EgenansattType withEgenansattKilde(String value) {
        this.setEgenansattKilde(value);
        return this;
    }

    public EgenansattType withEgenansattRegistrertAv(String value) {
        this.setEgenansattRegistrertAv(value);
        return this;
    }
}
