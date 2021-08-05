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
        name = "getiType",
        propOrder = { "getiLand", "getiKommune", "getiBydel", "getiRegel", "getiFraDato", "getiTilDato", "getiKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetiType {

    @XmlElement(
            required = true
    )
    protected String getiLand;
    @XmlElement(
            required = true
    )
    protected String getiKommune;
    @XmlElement(
            required = true
    )
    protected String getiBydel;
    @XmlElement(
            required = true
    )
    protected String getiRegel;
    @XmlElement(
            required = true
    )
    protected String getiFraDato;
    @XmlElement(
            required = true
    )
    protected String getiTilDato;
    @XmlElement(
            required = true
    )
    protected String getiKilde;

    public GetiType withGetiLand(String value) {
        this.setGetiLand(value);
        return this;
    }

    public GetiType withGetiKommune(String value) {
        this.setGetiKommune(value);
        return this;
    }

    public GetiType withGetiBydel(String value) {
        this.setGetiBydel(value);
        return this;
    }

    public GetiType withGetiRegel(String value) {
        this.setGetiRegel(value);
        return this;
    }

    public GetiType withGetiFraDato(String value) {
        this.setGetiFraDato(value);
        return this;
    }

    public GetiType withGetiTilDato(String value) {
        this.setGetiTilDato(value);
        return this;
    }

    public GetiType withGetiKilde(String value) {
        this.setGetiKilde(value);
        return this;
    }
}

