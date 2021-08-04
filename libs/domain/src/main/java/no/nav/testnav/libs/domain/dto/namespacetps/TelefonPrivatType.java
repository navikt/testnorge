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
        name = "telefonPrivatType",
        propOrder = { "tlfPrivatRetningslinje", "tlfPrivatNummer", "tlfPrivatFraDato", "tlfPrivatTilDato", "tlfPrivatKilde", "tlfPrivatRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TelefonPrivatType {

    @XmlElement(
            required = true
    )
    protected String tlfPrivatRetningslinje;
    @XmlElement(
            required = true
    )
    protected String tlfPrivatNummer;
    @XmlElement(
            required = true
    )
    protected String tlfPrivatFraDato;
    @XmlElement(
            required = true
    )
    protected String tlfPrivatTilDato;
    @XmlElement(
            required = true
    )
    protected String tlfPrivatKilde;
    @XmlElement(
            required = true
    )
    protected String tlfPrivatRegistrertAv;

    public TelefonPrivatType withTlfPrivatRetningslinje(String value) {
        this.setTlfPrivatRetningslinje(value);
        return this;
    }

    public TelefonPrivatType withTlfPrivatNummer(String value) {
        this.setTlfPrivatNummer(value);
        return this;
    }

    public TelefonPrivatType withTlfPrivatFraDato(String value) {
        this.setTlfPrivatFraDato(value);
        return this;
    }

    public TelefonPrivatType withTlfPrivatTilDato(String value) {
        this.setTlfPrivatTilDato(value);
        return this;
    }

    public TelefonPrivatType withTlfPrivatKilde(String value) {
        this.setTlfPrivatKilde(value);
        return this;
    }

    public TelefonPrivatType withTlfPrivatRegistrertAv(String value) {
        this.setTlfPrivatRegistrertAv(value);
        return this;
    }
}
