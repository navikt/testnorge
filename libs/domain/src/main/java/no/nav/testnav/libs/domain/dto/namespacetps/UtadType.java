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
        name = "utadType",
        propOrder = { "utadAdresse1", "utadAdresse2", "utadAdresse3", "utadAdresseDatoTom", "utadLand", "utadAdresseFraDato", "utadAdresseTilDato", "utadKilde", "utadRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UtadType {

    @XmlElement(
            required = true
    )
    protected String utadAdresse1;
    @XmlElement(
            required = true
    )
    protected String utadAdresse2;
    @XmlElement(
            required = true
    )
    protected String utadAdresse3;
    @XmlElement(
            required = true
    )
    protected String utadAdresseDatoTom;
    @XmlElement(
            required = true
    )
    protected String utadLand;
    @XmlElement(
            required = true
    )
    protected String utadAdresseFraDato;
    @XmlElement(
            required = true
    )
    protected String utadAdresseTilDato;
    @XmlElement(
            required = true
    )
    protected String utadKilde;
    @XmlElement(
            required = true
    )
    protected String utadRegistrertAv;

    public UtadType withUtadAdresse1(String value) {
        this.setUtadAdresse1(value);
        return this;
    }

    public UtadType withUtadAdresse2(String value) {
        this.setUtadAdresse2(value);
        return this;
    }

    public UtadType withUtadAdresse3(String value) {
        this.setUtadAdresse3(value);
        return this;
    }

    public UtadType withUtadAdresseDatoTom(String value) {
        this.setUtadAdresseDatoTom(value);
        return this;
    }

    public UtadType withUtadLand(String value) {
        this.setUtadLand(value);
        return this;
    }

    public UtadType withUtadAdresseFraDato(String value) {
        this.setUtadAdresseFraDato(value);
        return this;
    }

    public UtadType withUtadAdresseTilDato(String value) {
        this.setUtadAdresseTilDato(value);
        return this;
    }

    public UtadType withUtadKilde(String value) {
        this.setUtadKilde(value);
        return this;
    }

    public UtadType withUtadRegistrertAv(String value) {
        this.setUtadRegistrertAv(value);
        return this;
    }
}
