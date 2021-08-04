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
        name = "behovType",
        propOrder = { "brukerBehov", "brukerBehovFraDato", "brukerBehovTilDato", "brukerBehovKilde", "brukerBehovRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class BehovType {

    @XmlElement(
            required = true
    )
    protected String brukerBehov;
    @XmlElement(
            required = true
    )
    protected String brukerBehovFraDato;
    @XmlElement(
            required = true
    )
    protected String brukerBehovTilDato;
    @XmlElement(
            required = true
    )
    protected String brukerBehovKilde;
    @XmlElement(
            required = true
    )
    protected String brukerBehovRegistrertAv;

    public BehovType withBrukerBehov(String value) {
        this.setBrukerBehov(value);
        return this;
    }

    public BehovType withBrukerBehovFraDato(String value) {
        this.setBrukerBehovFraDato(value);
        return this;
    }

    public BehovType withBrukerBehovTilDato(String value) {
        this.setBrukerBehovTilDato(value);
        return this;
    }

    public BehovType withBrukerBehovKilde(String value) {
        this.setBrukerBehovKilde(value);
        return this;
    }

    public BehovType withBrukerBehovRegistrertAv(String value) {
        this.setBrukerBehovRegistrertAv(value);
        return this;
    }
}
