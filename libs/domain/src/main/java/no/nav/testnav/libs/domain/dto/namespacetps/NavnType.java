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
        name = "navnType",
        propOrder = { "forkortetNavn", "slektsNavn", "forNavn", "mellomNavn", "slektsNavnugift", "navnFraDato", "navnTilDato", "navnKilde", "navnRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class NavnType {

    @XmlElement(
            required = true
    )
    protected String forkortetNavn;
    @XmlElement(
            required = true
    )
    protected String slektsNavn;
    @XmlElement(
            required = true
    )
    protected String forNavn;
    @XmlElement(
            required = true
    )
    protected String mellomNavn;
    @XmlElement(
            required = true
    )
    protected String slektsNavnugift;
    @XmlElement(
            required = true
    )
    protected String navnFraDato;
    @XmlElement(
            required = true
    )
    protected String navnTilDato;
    @XmlElement(
            required = true
    )
    protected String navnKilde;
    @XmlElement(
            required = true
    )
    protected String navnRegistrertAv;

    public NavnType withForkortetNavn(String value) {
        this.setForkortetNavn(value);
        return this;
    }

    public NavnType withSlektsNavn(String value) {
        this.setSlektsNavn(value);
        return this;
    }

    public NavnType withForNavn(String value) {
        this.setForNavn(value);
        return this;
    }

    public NavnType withMellomNavn(String value) {
        this.setMellomNavn(value);
        return this;
    }

    public NavnType withSlektsNavnugift(String value) {
        this.setSlektsNavnugift(value);
        return this;
    }

    public NavnType withNavnFraDato(String value) {
        this.setNavnFraDato(value);
        return this;
    }

    public NavnType withNavnTilDato(String value) {
        this.setNavnTilDato(value);
        return this;
    }

    public NavnType withNavnKilde(String value) {
        this.setNavnKilde(value);
        return this;
    }

    public NavnType withNavnRegistrertAv(String value) {
        this.setNavnRegistrertAv(value);
        return this;
    }
}
