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
        name = "personStatusType",
        propOrder = { "personStatus", "personStatusFraDato", "personStatusTilDato", "personStatusKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PersonStatusType {

    @XmlElement(
            required = true
    )
    protected String personStatus;
    @XmlElement(
            required = true
    )
    protected String personStatusFraDato;
    @XmlElement(
            required = true
    )
    protected String personStatusTilDato;
    @XmlElement(
            required = true
    )
    protected String personStatusKilde;

    public PersonStatusType withPersonStatus(String value) {
        this.setPersonStatus(value);
        return this;
    }

    public PersonStatusType withPersonStatusFraDato(String value) {
        this.setPersonStatusFraDato(value);
        return this;
    }

    public PersonStatusType withPersonStatusTilDato(String value) {
        this.setPersonStatusTilDato(value);
        return this;
    }

    public PersonStatusType withPersonStatusKilde(String value) {
        this.setPersonStatusKilde(value);
        return this;
    }
}
