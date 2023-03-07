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
        name = "prioritertadresseType",
        propOrder = { "prioritertAdresseType", "prioritertAdresseFraDato", "prioritertAdresseTilDato", "prioritertAdresseKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PrioritertadresseType {

    @XmlElement(
            required = true
    )
    protected String prioritertAdresseType;
    @XmlElement(
            required = true
    )
    protected String prioritertAdresseFraDato;
    @XmlElement(
            required = true
    )
    protected String prioritertAdresseTilDato;
    @XmlElement(
            required = true
    )
    protected String prioritertAdresseKilde;

    public PrioritertadresseType withPrioritertAdresseType(String value) {
        this.setPrioritertAdresseType(value);
        return this;
    }

    public PrioritertadresseType withPrioritertAdresseFraDato(String value) {
        this.setPrioritertAdresseFraDato(value);
        return this;
    }

    public PrioritertadresseType withPrioritertAdresseTilDato(String value) {
        this.setPrioritertAdresseTilDato(value);
        return this;
    }

    public PrioritertadresseType withPrioritertAdresseKilde(String value) {
        this.setPrioritertAdresseKilde(value);
        return this;
    }
}
