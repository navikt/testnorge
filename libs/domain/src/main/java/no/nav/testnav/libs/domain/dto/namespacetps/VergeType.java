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
        name = "vergeType",
        propOrder = { "vergeSaksid", "vergeId", "vergeType", "vergeSakstype", "vergeMandattype", "vergeEmbete", "vergeVedtakDato", "vergeFnr", "vergeFraDato", "vergeTilDato", "vergeKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class VergeType {

    @XmlElement(
            required = true
    )
    protected String vergeSaksid;
    @XmlElement(
            required = true
    )
    protected String vergeId;
    @XmlElement(
            required = true
    )
    protected String vergeType;
    @XmlElement(
            required = true
    )
    protected String vergeSakstype;
    @XmlElement(
            required = true
    )
    protected String vergeMandattype;
    @XmlElement(
            required = true
    )
    protected String vergeEmbete;
    @XmlElement(
            required = true
    )
    protected String vergeVedtakDato;
    @XmlElement(
            required = true
    )
    protected String vergeFnr;
    @XmlElement(
            required = true
    )
    protected String vergeFraDato;
    @XmlElement(
            required = true
    )
    protected String vergeTilDato;
    @XmlElement(
            required = true
    )
    protected String vergeKilde;

    public VergeType withVergeSaksid(String value) {
        this.setVergeSaksid(value);
        return this;
    }

    public VergeType withVergeId(String value) {
        this.setVergeId(value);
        return this;
    }

    public VergeType withVergeType(String value) {
        this.setVergeType(value);
        return this;
    }

    public VergeType withVergeSakstype(String value) {
        this.setVergeSakstype(value);
        return this;
    }

    public VergeType withVergeMandattype(String value) {
        this.setVergeMandattype(value);
        return this;
    }

    public VergeType withVergeEmbete(String value) {
        this.setVergeEmbete(value);
        return this;
    }

    public VergeType withVergeVedtakDato(String value) {
        this.setVergeVedtakDato(value);
        return this;
    }

    public VergeType withVergeFnr(String value) {
        this.setVergeFnr(value);
        return this;
    }

    public VergeType withVergeFraDato(String value) {
        this.setVergeFraDato(value);
        return this;
    }

    public VergeType withVergeTilDato(String value) {
        this.setVergeTilDato(value);
        return this;
    }

    public VergeType withVergeKilde(String value) {
        this.setVergeKilde(value);
        return this;
    }
}
