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
        name = "relasjonType",
        propOrder = { "relasjonIdent", "relasjonIdentType", "relasjonIdentStatus", "relasjonRolle", "relasjonFraDato", "relasjonTilDato", "relasjonKilde", "relasjonRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RelasjonType {

    @XmlElement(
            required = true
    )
    protected String relasjonIdent;
    @XmlElement(
            required = true
    )
    protected String relasjonIdentType;
    @XmlElement(
            required = true
    )
    protected String relasjonIdentStatus;
    @XmlElement(
            required = true
    )
    protected String relasjonRolle;
    @XmlElement(
            required = true
    )
    protected String relasjonFraDato;
    @XmlElement(
            required = true
    )
    protected String relasjonTilDato;
    @XmlElement(
            required = true
    )
    protected String relasjonKilde;
    @XmlElement(
            required = true
    )
    protected String relasjonRegistrertAv;

    public RelasjonType withRelasjonIdent(String value) {
        this.setRelasjonIdent(value);
        return this;
    }

    public RelasjonType withRelasjonIdentType(String value) {
        this.setRelasjonIdentType(value);
        return this;
    }

    public RelasjonType withRelasjonIdentStatus(String value) {
        this.setRelasjonIdentStatus(value);
        return this;
    }

    public RelasjonType withRelasjonRolle(String value) {
        this.setRelasjonRolle(value);
        return this;
    }

    public RelasjonType withRelasjonFraDato(String value) {
        this.setRelasjonFraDato(value);
        return this;
    }

    public RelasjonType withRelasjonTilDato(String value) {
        this.setRelasjonTilDato(value);
        return this;
    }

    public RelasjonType withRelasjonKilde(String value) {
        this.setRelasjonKilde(value);
        return this;
    }

    public RelasjonType withRelasjonRegistrertAv(String value) {
        this.setRelasjonRegistrertAv(value);
        return this;
    }
}
