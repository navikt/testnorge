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
        name = "dokumentType",
        propOrder = { "dokumentstatus", "dokumentutfyllendemelding", "dokumenttidspunkt", "dokumenthistorikk", "dokumentFraDato", "dokumentTilDato", "dokumentKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DokumentType {

    @XmlElement(
            required = true
    )
    protected String dokumentstatus;
    @XmlElement(
            required = true
    )
    protected String dokumentutfyllendemelding;
    @XmlElement(
            required = true
    )
    protected String dokumenttidspunkt;
    @XmlElement(
            required = true
    )
    protected String dokumenthistorikk;
    @XmlElement(
            required = true
    )
    protected String dokumentFraDato;
    @XmlElement(
            required = true
    )
    protected String dokumentTilDato;
    @XmlElement(
            required = true
    )
    protected String dokumentKilde;

    public DokumentType withDokumentstatus(String value) {
        this.setDokumentstatus(value);
        return this;
    }

    public DokumentType withDokumentutfyllendemelding(String value) {
        this.setDokumentutfyllendemelding(value);
        return this;
    }

    public DokumentType withDokumenttidspunkt(String value) {
        this.setDokumenttidspunkt(value);
        return this;
    }

    public DokumentType withDokumenthistorikk(String value) {
        this.setDokumenthistorikk(value);
        return this;
    }

    public DokumentType withDokumentFraDato(String value) {
        this.setDokumentFraDato(value);
        return this;
    }

    public DokumentType withDokumentTilDato(String value) {
        this.setDokumentTilDato(value);
        return this;
    }

    public DokumentType withDokumentKilde(String value) {
        this.setDokumentKilde(value);
        return this;
    }
}

