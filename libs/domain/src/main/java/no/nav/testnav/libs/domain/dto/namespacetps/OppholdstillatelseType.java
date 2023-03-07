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
        name = "oppholdstillatelseType",
        propOrder = { "oppholdsTillatelse", "oppholdsTillatelseFraDato", "oppholdsTillatelseTilDato", "oppholdsTillatelseKilde", "oppholdsTillatelseRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class OppholdstillatelseType {

    @XmlElement(
            required = true
    )
    protected String oppholdsTillatelse;
    @XmlElement(
            required = true
    )
    protected String oppholdsTillatelseFraDato;
    @XmlElement(
            required = true
    )
    protected String oppholdsTillatelseTilDato;
    @XmlElement(
            required = true
    )
    protected String oppholdsTillatelseKilde;
    @XmlElement(
            required = true
    )
    protected String oppholdsTillatelseRegistrertAv;

    public OppholdstillatelseType withOppholdsTillatelse(String value) {
        this.setOppholdsTillatelse(value);
        return this;
    }

    public OppholdstillatelseType withOppholdsTillatelseFraDato(String value) {
        this.setOppholdsTillatelseFraDato(value);
        return this;
    }

    public OppholdstillatelseType withOppholdsTillatelseTilDato(String value) {
        this.setOppholdsTillatelseTilDato(value);
        return this;
    }

    public OppholdstillatelseType withOppholdsTillatelseKilde(String value) {
        this.setOppholdsTillatelseKilde(value);
        return this;
    }

    public OppholdstillatelseType withOppholdsTillatelseRegistrertAv(String value) {
        this.setOppholdsTillatelseRegistrertAv(value);
        return this;
    }
}
