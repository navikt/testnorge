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
        name = "foreldreansvarType",
        propOrder = { "foreldreAnsvar", "foreldreAnsvarFraDato", "foreldreAnsvarTilDato", "foreldreAnsvarKilde", "forreldreAnsvarRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ForeldreansvarType {

    @XmlElement(
            required = true
    )
    protected String foreldreAnsvar;
    @XmlElement(
            required = true
    )
    protected String foreldreAnsvarFraDato;
    @XmlElement(
            required = true
    )
    protected String foreldreAnsvarTilDato;
    @XmlElement(
            required = true
    )
    protected String foreldreAnsvarKilde;
    @XmlElement(
            required = true
    )
    protected String forreldreAnsvarRegistrertAv;

    public ForeldreansvarType withForeldreAnsvar(String value) {
        this.setForeldreAnsvar(value);
        return this;
    }

    public ForeldreansvarType withForeldreAnsvarFraDato(String value) {
        this.setForeldreAnsvarFraDato(value);
        return this;
    }

    public ForeldreansvarType withForeldreAnsvarTilDato(String value) {
        this.setForeldreAnsvarTilDato(value);
        return this;
    }

    public ForeldreansvarType withForeldreAnsvarKilde(String value) {
        this.setForeldreAnsvarKilde(value);
        return this;
    }

    public ForeldreansvarType withForreldreAnsvarRegistrertAv(String value) {
        this.setForreldreAnsvarRegistrertAv(value);
        return this;
    }
}

