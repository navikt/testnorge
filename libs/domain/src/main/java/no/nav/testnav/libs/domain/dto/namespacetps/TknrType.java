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
        name = "tknrType",
        propOrder = { "tknrPerson", "tknrFraDato", "tknrTilDato", "tknrKilde", "tknregistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TknrType {

    @XmlElement(
            required = true
    )
    protected String tknrPerson;
    @XmlElement(
            required = true
    )
    protected String tknrFraDato;
    @XmlElement(
            required = true
    )
    protected String tknrTilDato;
    @XmlElement(
            required = true
    )
    protected String tknrKilde;
    @XmlElement(
            required = true
    )
    protected String tknregistrertAv;

    public TknrType withTknrPerson(String value) {
        this.setTknrPerson(value);
        return this;
    }

    public TknrType withTknrFraDato(String value) {
        this.setTknrFraDato(value);
        return this;
    }

    public TknrType withTknrTilDato(String value) {
        this.setTknrTilDato(value);
        return this;
    }

    public TknrType withTknrKilde(String value) {
        this.setTknrKilde(value);
        return this;
    }

    public TknrType withTknregistrertAv(String value) {
        this.setTknregistrertAv(value);
        return this;
    }
}
