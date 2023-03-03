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
        name = "dodType",
        propOrder = { "datoDod", "dodDatoReg", "dodKilde", "dodRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DodType {

    @XmlElement(
            required = true
    )
    protected String datoDod;
    @XmlElement(
            required = true
    )
    protected String dodDatoReg;
    @XmlElement(
            required = true
    )
    protected String dodKilde;
    @XmlElement(
            required = true
    )
    protected String dodRegistrertAv;

    public DodType withDatoDod(String value) {
        this.setDatoDod(value);
        return this;
    }

    public DodType withDodDatoReg(String value) {
        this.setDodDatoReg(value);
        return this;
    }

    public DodType withDodKilde(String value) {
        this.setDodKilde(value);
        return this;
    }

    public DodType withDodRegistrertAv(String value) {
        this.setDodRegistrertAv(value);
        return this;
    }
}

