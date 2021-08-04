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
        name = "gironummerType",
        propOrder = { "gironummer", "gironummerFraDato", "gironummerTilDato", "gironummerKilde", "gironummeRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GironummerType {

    @XmlElement(
            required = true
    )
    protected String gironummer;
    @XmlElement(
            required = true
    )
    protected String gironummerFraDato;
    @XmlElement(
            required = true
    )
    protected String gironummerTilDato;
    @XmlElement(
            required = true
    )
    protected String gironummerKilde;
    @XmlElement(
            required = true
    )
    protected String gironummeRegistrertAv;

    public GironummerType withGironummer(String value) {
        this.setGironummer(value);
        return this;
    }

    public GironummerType withGironummerFraDato(String value) {
        this.setGironummerFraDato(value);
        return this;
    }

    public GironummerType withGironummerTilDato(String value) {
        this.setGironummerTilDato(value);
        return this;
    }

    public GironummerType withGironummerKilde(String value) {
        this.setGironummerKilde(value);
        return this;
    }

    public GironummerType withGironummeRegistrertAv(String value) {
        this.setGironummeRegistrertAv(value);
        return this;
    }
}

