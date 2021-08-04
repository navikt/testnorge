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
        name = "tiltakType",
        propOrder = { "tiltak", "tiltakFraDato", "tiltakTilDato", "tiltakKilde", "tiltakRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TiltakType {

    @XmlElement(
            required = true
    )
    protected String tiltak;
    @XmlElement(
            required = true
    )
    protected String tiltakFraDato;
    @XmlElement(
            required = true
    )
    protected String tiltakTilDato;
    @XmlElement(
            required = true
    )
    protected String tiltakKilde;
    @XmlElement(
            required = true
    )
    protected String tiltakRegistrertAv;

    public TiltakType withTiltak(String value) {
        this.setTiltak(value);
        return this;
    }

    public TiltakType withTiltakFraDato(String value) {
        this.setTiltakFraDato(value);
        return this;
    }

    public TiltakType withTiltakTilDato(String value) {
        this.setTiltakTilDato(value);
        return this;
    }

    public TiltakType withTiltakKilde(String value) {
        this.setTiltakKilde(value);
        return this;
    }

    public TiltakType withTiltakRegistrertAv(String value) {
        this.setTiltakRegistrertAv(value);
        return this;
    }
}
