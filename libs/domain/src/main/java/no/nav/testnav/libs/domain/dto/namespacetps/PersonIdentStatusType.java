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
        name = "personIdentStatusType",
        propOrder = { "personIdentStatus", "personIdentStatusFraDato", "personIdentStatusTilDato", "personIdentStatusKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PersonIdentStatusType {

    @XmlElement(
            required = true
    )
    protected String personIdentStatus;
    @XmlElement(
            required = true
    )
    protected String personIdentStatusFraDato;
    @XmlElement(
            required = true
    )
    protected String personIdentStatusTilDato;
    @XmlElement(
            required = true
    )
    protected String personIdentStatusKilde;

    public PersonIdentStatusType withPersonIdentStatus(String value) {
        this.setPersonIdentStatus(value);
        return this;
    }

    public PersonIdentStatusType withPersonIdentStatusFraDato(String value) {
        this.setPersonIdentStatusFraDato(value);
        return this;
    }

    public PersonIdentStatusType withPersonIdentStatusTilDato(String value) {
        this.setPersonIdentStatusTilDato(value);
        return this;
    }

    public PersonIdentStatusType withPersonIdentStatusKilde(String value) {
        this.setPersonIdentStatusKilde(value);
        return this;
    }
}
