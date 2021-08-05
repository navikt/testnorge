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
        name = "tilleggType",
        propOrder = { "tilleggAdresse1", "tilleggAdresse2", "tilleggAdresse3", "tilleggPostnr", "tilleggAdresseDatoTom", "tilleggKommunenr", "tilleggGateKode", "tilleggHusnummer", "tilleggHusbokstav",
                "tilleggBolignummer", "tilleggBydel", "tilleggPostboksnr", "tilleggPostboksAnlegg", "tilleggAdresseFraDato", "tilleggAdresseTilDato", "tilleggKilde", "tilleggRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TilleggType {

    @XmlElement(
            required = true
    )
    protected String tilleggAdresse1;
    @XmlElement(
            required = true
    )
    protected String tilleggAdresse2;
    @XmlElement(
            required = true
    )
    protected String tilleggAdresse3;
    @XmlElement(
            required = true
    )
    protected String tilleggPostnr;
    @XmlElement(
            required = true
    )
    protected String tilleggAdresseDatoTom;
    @XmlElement(
            required = true
    )
    protected String tilleggKommunenr;
    @XmlElement(
            required = true
    )
    protected String tilleggGateKode;
    @XmlElement(
            required = true
    )
    protected String tilleggHusnummer;
    @XmlElement(
            required = true
    )
    protected String tilleggHusbokstav;
    @XmlElement(
            required = true
    )
    protected String tilleggBolignummer;
    @XmlElement(
            required = true
    )
    protected String tilleggBydel;
    @XmlElement(
            required = true
    )
    protected String tilleggPostboksnr;
    @XmlElement(
            required = true
    )
    protected String tilleggPostboksAnlegg;
    @XmlElement(
            required = true
    )
    protected String tilleggAdresseFraDato;
    @XmlElement(
            required = true
    )
    protected String tilleggAdresseTilDato;
    @XmlElement(
            required = true
    )
    protected String tilleggKilde;
    @XmlElement(
            required = true
    )
    protected String tilleggRegistrertAv;

    public TilleggType withTilleggAdresse1(String value) {
        this.setTilleggAdresse1(value);
        return this;
    }

    public TilleggType withTilleggAdresse2(String value) {
        this.setTilleggAdresse2(value);
        return this;
    }

    public TilleggType withTilleggAdresse3(String value) {
        this.setTilleggAdresse3(value);
        return this;
    }

    public TilleggType withTilleggPostnr(String value) {
        this.setTilleggPostnr(value);
        return this;
    }

    public TilleggType withTilleggAdresseDatoTom(String value) {
        this.setTilleggAdresseDatoTom(value);
        return this;
    }

    public TilleggType withTilleggKommunenr(String value) {
        this.setTilleggKommunenr(value);
        return this;
    }

    public TilleggType withTilleggGateKode(String value) {
        this.setTilleggGateKode(value);
        return this;
    }

    public TilleggType withTilleggHusnummer(String value) {
        this.setTilleggHusnummer(value);
        return this;
    }

    public TilleggType withTilleggHusbokstav(String value) {
        this.setTilleggHusbokstav(value);
        return this;
    }

    public TilleggType withTilleggBolignummer(String value) {
        this.setTilleggBolignummer(value);
        return this;
    }

    public TilleggType withTilleggBydel(String value) {
        this.setTilleggBydel(value);
        return this;
    }

    public TilleggType withTilleggPostboksnr(String value) {
        this.setTilleggPostboksnr(value);
        return this;
    }

    public TilleggType withTilleggPostboksAnlegg(String value) {
        this.setTilleggPostboksAnlegg(value);
        return this;
    }

    public TilleggType withTilleggAdresseFraDato(String value) {
        this.setTilleggAdresseFraDato(value);
        return this;
    }

    public TilleggType withTilleggAdresseTilDato(String value) {
        this.setTilleggAdresseTilDato(value);
        return this;
    }

    public TilleggType withTilleggKilde(String value) {
        this.setTilleggKilde(value);
        return this;
    }

    public TilleggType withTilleggRegistrertAv(String value) {
        this.setTilleggRegistrertAv(value);
        return this;
    }
}
