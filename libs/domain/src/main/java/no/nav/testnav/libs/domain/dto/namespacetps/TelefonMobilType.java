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
        name = "telefonMobilType",
        propOrder = { "tlfMobilRetningslinje", "tlfMobilNummer", "tlfMobilFraDato", "tlfMobilTilDato", "tlfMobilKilde", "tlfMobilRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TelefonMobilType {

    @XmlElement(
            required = true
    )
    protected String tlfMobilRetningslinje;
    @XmlElement(
            required = true
    )
    protected String tlfMobilNummer;
    @XmlElement(
            required = true
    )
    protected String tlfMobilFraDato;
    @XmlElement(
            required = true
    )
    protected String tlfMobilTilDato;
    @XmlElement(
            required = true
    )
    protected String tlfMobilKilde;
    @XmlElement(
            required = true
    )
    protected String tlfMobilRegistrertAv;

    public TelefonMobilType withTlfMobilRetningslinje(String value) {
        this.setTlfMobilRetningslinje(value);
        return this;
    }

    public TelefonMobilType withTlfMobilNummer(String value) {
        this.setTlfMobilNummer(value);
        return this;
    }

    public TelefonMobilType withTlfMobilFraDato(String value) {
        this.setTlfMobilFraDato(value);
        return this;
    }

    public TelefonMobilType withTlfMobilTilDato(String value) {
        this.setTlfMobilTilDato(value);
        return this;
    }

    public TelefonMobilType withTlfMobilKilde(String value) {
        this.setTlfMobilKilde(value);
        return this;
    }

    public TelefonMobilType withTlfMobilRegistrertAv(String value) {
        this.setTlfMobilRegistrertAv(value);
        return this;
    }
}
