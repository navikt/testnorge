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
        name = "spesRegType",
        propOrder = { "spesReg", "spesRegFraDato", "spesRegTilDato", "spesRegKilde", "spesRegRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SpesRegType {

    @XmlElement(
            required = true
    )
    protected String spesReg;
    @XmlElement(
            required = true
    )
    protected String spesRegFraDato;
    @XmlElement(
            required = true
    )
    protected String spesRegTilDato;
    @XmlElement(
            required = true
    )
    protected String spesRegKilde;
    @XmlElement(
            required = true
    )
    protected String spesRegRegistrertAv;

    public SpesRegType withSpesReg(String value) {
        this.setSpesReg(value);
        return this;
    }

    public SpesRegType withSpesRegFraDato(String value) {
        this.setSpesRegFraDato(value);
        return this;
    }

    public SpesRegType withSpesRegTilDato(String value) {
        this.setSpesRegTilDato(value);
        return this;
    }

    public SpesRegType withSpesRegKilde(String value) {
        this.setSpesRegKilde(value);
        return this;
    }

    public SpesRegType withSpesRegRegistrertAv(String value) {
        this.setSpesRegRegistrertAv(value);
        return this;
    }
}
