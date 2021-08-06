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
        name = "telefonJobbType",
        propOrder = { "tlfJobbRetningslinje", "tlfJobbNummer", "tlfJobbFraDato", "tlfJobbTilDato", "tlfJobbKilde", "tlfJobbRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TelefonJobbType {

    @XmlElement(
            required = true
    )
    protected String tlfJobbRetningslinje;
    @XmlElement(
            required = true
    )
    protected String tlfJobbNummer;
    @XmlElement(
            required = true
    )
    protected String tlfJobbFraDato;
    @XmlElement(
            required = true
    )
    protected String tlfJobbTilDato;
    @XmlElement(
            required = true
    )
    protected String tlfJobbKilde;
    @XmlElement(
            required = true
    )
    protected String tlfJobbRegistrertAv;

    public TelefonJobbType withTlfJobbRetningslinje(String value) {
        this.setTlfJobbRetningslinje(value);
        return this;
    }

    public TelefonJobbType withTlfJobbNummer(String value) {
        this.setTlfJobbNummer(value);
        return this;
    }

    public TelefonJobbType withTlfJobbFraDato(String value) {
        this.setTlfJobbFraDato(value);
        return this;
    }

    public TelefonJobbType withTlfJobbTilDato(String value) {
        this.setTlfJobbTilDato(value);
        return this;
    }

    public TelefonJobbType withTlfJobbKilde(String value) {
        this.setTlfJobbKilde(value);
        return this;
    }

    public TelefonJobbType withTlfJobbRegistrertAv(String value) {
        this.setTlfJobbRegistrertAv(value);
        return this;
    }
}
