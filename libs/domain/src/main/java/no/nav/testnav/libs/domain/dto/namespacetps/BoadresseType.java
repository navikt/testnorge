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
        name = "boadresseType",
        propOrder = { "boAdresse", "boKodeLand", "boKommune", "boPostnr", "boBydel", "boAdressetillegg", "booffaGateKode", "booffaHusnr", "booffaBokstav", "booffaBolignr", "bomatrGardsnr", "bomatrBruksnr",
                "bomatrFestenr", "bomatrUndernr", "boAdresseFraDato", "boAdresseTilDato", "boAdresseKilde" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class BoadresseType {

    @XmlElement(
            required = true
    )
    protected String boAdresse;
    @XmlElement(
            required = true
    )
    protected String boKodeLand;
    @XmlElement(
            required = true
    )
    protected String boKommune;
    @XmlElement(
            required = true
    )
    protected String boPostnr;
    @XmlElement(
            required = true
    )
    protected String boBydel;
    @XmlElement(
            required = true
    )
    protected String boAdressetillegg;
    @XmlElement(
            required = true
    )
    protected String booffaGateKode;
    @XmlElement(
            required = true
    )
    protected String booffaHusnr;
    @XmlElement(
            required = true
    )
    protected String booffaBokstav;
    @XmlElement(
            required = true
    )
    protected String booffaBolignr;
    @XmlElement(
            required = true
    )
    protected String bomatrGardsnr;
    @XmlElement(
            required = true
    )
    protected String bomatrBruksnr;
    @XmlElement(
            required = true
    )
    protected String bomatrFestenr;
    @XmlElement(
            required = true
    )
    protected String bomatrUndernr;
    @XmlElement(
            required = true
    )
    protected String boAdresseFraDato;
    @XmlElement(
            required = true
    )
    protected String boAdresseTilDato;
    @XmlElement(
            required = true
    )
    protected String boAdresseKilde;

    public BoadresseType withBoAdresse(String value) {
        this.setBoAdresse(value);
        return this;
    }

    public BoadresseType withBoKodeLand(String value) {
        this.setBoKodeLand(value);
        return this;
    }

    public BoadresseType withBoKommune(String value) {
        this.setBoKommune(value);
        return this;
    }

    public BoadresseType withBoPostnr(String value) {
        this.setBoPostnr(value);
        return this;
    }

    public BoadresseType withBoBydel(String value) {
        this.setBoBydel(value);
        return this;
    }

    public BoadresseType withBoAdressetillegg(String value) {
        this.setBoAdressetillegg(value);
        return this;
    }

    public BoadresseType withBooffaGateKode(String value) {
        this.setBooffaGateKode(value);
        return this;
    }

    public BoadresseType withBooffaHusnr(String value) {
        this.setBooffaHusnr(value);
        return this;
    }

    public BoadresseType withBooffaBokstav(String value) {
        this.setBooffaBokstav(value);
        return this;
    }

    public BoadresseType withBooffaBolignr(String value) {
        this.setBooffaBolignr(value);
        return this;
    }

    public BoadresseType withBomatrGardsnr(String value) {
        this.setBomatrGardsnr(value);
        return this;
    }

    public BoadresseType withBomatrBruksnr(String value) {
        this.setBomatrBruksnr(value);
        return this;
    }

    public BoadresseType withBomatrFestenr(String value) {
        this.setBomatrFestenr(value);
        return this;
    }

    public BoadresseType withBomatrUndernr(String value) {
        this.setBomatrUndernr(value);
        return this;
    }

    public BoadresseType withBoAdresseFraDato(String value) {
        this.setBoAdresseFraDato(value);
        return this;
    }

    public BoadresseType withBoAdresseTilDato(String value) {
        this.setBoAdresseTilDato(value);
        return this;
    }

    public BoadresseType withBoAdresseKilde(String value) {
        this.setBoAdresseKilde(value);
        return this;
    }
}

