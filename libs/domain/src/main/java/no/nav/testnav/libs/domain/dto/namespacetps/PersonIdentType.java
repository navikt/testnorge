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
        name = "personIdentType",
        propOrder = { "personIdent", "personIdenttype", "personIdentneste", "personIdentFraDato", "personIdentTilDato", "personIdentKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PersonIdentType {

    @XmlElement(
            required = true
    )
    protected String personIdent;
    @XmlElement(
            required = true
    )
    protected String personIdenttype;
    @XmlElement(
            required = true
    )
    protected String personIdentneste;
    @XmlElement(
            required = true
    )
    protected String personIdentFraDato;
    @XmlElement(
            required = true
    )
    protected String personIdentTilDato;
    @XmlElement(
            required = true
    )
    protected String personIdentKilde;

    public PersonIdentType withPersonIdent(String value) {
        this.setPersonIdent(value);
        return this;
    }

    public PersonIdentType withPersonIdenttype(String value) {
        this.setPersonIdenttype(value);
        return this;
    }

    public PersonIdentType withPersonIdentneste(String value) {
        this.setPersonIdentneste(value);
        return this;
    }

    public PersonIdentType withPersonIdentFraDato(String value) {
        this.setPersonIdentFraDato(value);
        return this;
    }

    public PersonIdentType withPersonIdentTilDato(String value) {
        this.setPersonIdentTilDato(value);
        return this;
    }

    public PersonIdentType withPersonIdentKilde(String value) {
        this.setPersonIdentKilde(value);
        return this;
    }
}

