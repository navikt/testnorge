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
        name = "migrasjonType",
        propOrder = { "migrasjon", "migrasjonLand", "migrasjonFraDato", "migrasjonTilDato", "migrasjonKilde", "migrasjonRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class MigrasjonType {

    @XmlElement(
            required = true
    )
    protected String migrasjon;
    @XmlElement(
            required = true
    )
    protected String migrasjonLand;
    @XmlElement(
            required = true
    )
    protected String migrasjonFraDato;
    @XmlElement(
            required = true
    )
    protected String migrasjonTilDato;
    @XmlElement(
            required = true
    )
    protected String migrasjonKilde;
    @XmlElement(
            required = true
    )
    protected String migrasjonRegistrertAv;

    public MigrasjonType withMigrasjon(String value) {
        this.setMigrasjon(value);
        return this;
    }

    public MigrasjonType withMigrasjonLand(String value) {
        this.setMigrasjonLand(value);
        return this;
    }

    public MigrasjonType withMigrasjonFraDato(String value) {
        this.setMigrasjonFraDato(value);
        return this;
    }

    public MigrasjonType withMigrasjonTilDato(String value) {
        this.setMigrasjonTilDato(value);
        return this;
    }

    public MigrasjonType withMigrasjonKilde(String value) {
        this.setMigrasjonKilde(value);
        return this;
    }

    public MigrasjonType withMigrasjonRegistrertAv(String value) {
        this.setMigrasjonRegistrertAv(value);
        return this;
    }
}

