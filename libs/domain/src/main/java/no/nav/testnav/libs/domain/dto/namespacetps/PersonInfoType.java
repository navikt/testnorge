package no.nav.testnav.libs.domain.dto.namespacetps;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "personInfoType",
        propOrder = { "personKjonn", "personDatofodt", "personFodtLand", "personFodtKommune", "personFraDato", "personTilDato", "personKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PersonInfoType {

    @XmlElement(
            required = true
    )
    protected String personKjonn;
    @XmlElement(
            required = true
    )
    protected String personDatofodt;
    @XmlElement(
            required = true
    )
    protected String personFodtLand;
    @XmlElement(
            required = true
    )
    protected String personFodtKommune;
    @XmlElement(
            required = true
    )
    protected String personFraDato;
    @XmlElement(
            required = true
    )
    protected String personTilDato;
    @XmlElement(
            required = true
    )
    protected String personKilde;

    public PersonInfoType withPersonKjonn(String value) {
        this.setPersonKjonn(value);
        return this;
    }

    public PersonInfoType withPersonDatofodt(String value) {
        this.setPersonDatofodt(value);
        return this;
    }

    public PersonInfoType withPersonFodtLand(String value) {
        this.setPersonFodtLand(value);
        return this;
    }

    public PersonInfoType withPersonFodtKommune(String value) {
        this.setPersonFodtKommune(value);
        return this;
    }

    public PersonInfoType withPersonFraDato(String value) {
        this.setPersonFraDato(value);
        return this;
    }

    public PersonInfoType withPersonTilDato(String value) {
        this.setPersonTilDato(value);
        return this;
    }

    public PersonInfoType withPersonKilde(String value) {
        this.setPersonKilde(value);
        return this;
    }
}

