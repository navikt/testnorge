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
        name = "utlandinfoType",
        propOrder = { "utlandinfoIdOff", "utlandinfoLand", "utlandinfoSektor", "utlandinfoInstitusjon", "utlandinfoKildePin", "utlandinfoFamnavnFodt", "utlandinfoFornavnFodt", "utlandinfoFodested",
                "utlandinfoFarsFamnavn", "utlandinfoMorsFamnavn", "utlandinfoFarsFornavn", "utlandinfoMorsFornavn", "utlandinfoNasjonalitet", "utlandinfoSedRef", "utlandinfoNasjonalId", "utlandinfoInstitusjonNavn",
                "utlandinfoFraDato", "utlandinfoTilDato", "utlandinfoKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UtlandinfoType {

    @XmlElement(
            required = true
    )
    protected String utlandinfoIdOff;
    @XmlElement(
            required = true
    )
    protected String utlandinfoLand;
    @XmlElement(
            required = true
    )
    protected String utlandinfoSektor;
    @XmlElement(
            required = true
    )
    protected String utlandinfoInstitusjon;
    @XmlElement(
            required = true
    )
    protected String utlandinfoKildePin;
    @XmlElement(
            required = true
    )
    protected String utlandinfoFamnavnFodt;
    @XmlElement(
            required = true
    )
    protected String utlandinfoFornavnFodt;
    @XmlElement(
            required = true
    )
    protected String utlandinfoFodested;
    @XmlElement(
            required = true
    )
    protected String utlandinfoFarsFamnavn;
    @XmlElement(
            required = true
    )
    protected String utlandinfoMorsFamnavn;
    @XmlElement(
            required = true
    )
    protected String utlandinfoFarsFornavn;
    @XmlElement(
            required = true
    )
    protected String utlandinfoMorsFornavn;
    @XmlElement(
            required = true
    )
    protected String utlandinfoNasjonalitet;
    @XmlElement(
            required = true
    )
    protected String utlandinfoSedRef;
    @XmlElement(
            required = true
    )
    protected String utlandinfoNasjonalId;
    @XmlElement(
            required = true
    )
    protected String utlandinfoInstitusjonNavn;
    @XmlElement(
            required = true
    )
    protected String utlandinfoFraDato;
    @XmlElement(
            required = true
    )
    protected String utlandinfoTilDato;
    @XmlElement(
            required = true
    )
    protected String utlandinfoKilde;

    public UtlandinfoType withUtlandinfoIdOff(String value) {
        this.setUtlandinfoIdOff(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoLand(String value) {
        this.setUtlandinfoLand(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoSektor(String value) {
        this.setUtlandinfoSektor(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoInstitusjon(String value) {
        this.setUtlandinfoInstitusjon(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoKildePin(String value) {
        this.setUtlandinfoKildePin(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoFamnavnFodt(String value) {
        this.setUtlandinfoFamnavnFodt(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoFornavnFodt(String value) {
        this.setUtlandinfoFornavnFodt(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoFodested(String value) {
        this.setUtlandinfoFodested(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoFarsFamnavn(String value) {
        this.setUtlandinfoFarsFamnavn(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoMorsFamnavn(String value) {
        this.setUtlandinfoMorsFamnavn(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoFarsFornavn(String value) {
        this.setUtlandinfoFarsFornavn(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoMorsFornavn(String value) {
        this.setUtlandinfoMorsFornavn(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoNasjonalitet(String value) {
        this.setUtlandinfoNasjonalitet(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoSedRef(String value) {
        this.setUtlandinfoSedRef(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoNasjonalId(String value) {
        this.setUtlandinfoNasjonalId(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoInstitusjonNavn(String value) {
        this.setUtlandinfoInstitusjonNavn(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoFraDato(String value) {
        this.setUtlandinfoFraDato(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoTilDato(String value) {
        this.setUtlandinfoTilDato(value);
        return this;
    }

    public UtlandinfoType withUtlandinfoKilde(String value) {
        this.setUtlandinfoKilde(value);
        return this;
    }
}
