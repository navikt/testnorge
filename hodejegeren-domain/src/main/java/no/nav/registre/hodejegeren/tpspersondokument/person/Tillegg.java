
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i tilleggadressegruppa.
 *
 * <p>Java class for tilleggType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tilleggType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tilleggAdresse1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggAdresse2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggAdresse3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggPostnr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggAdresseDatoTom" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggKommunenr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggGateKode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggHusnummer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggHusbokstav" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggBolignummer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggBydel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggPostboksnr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggPostboksAnlegg" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggAdresseFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggAdresseTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tilleggRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tillegg", propOrder = {
        "tilleggAdresse1",
        "tilleggAdresse2",
        "tilleggAdresse3",
        "tilleggPostnr",
        "tilleggAdresseDatoTom",
        "tilleggKommunenr",
        "tilleggGateKode",
        "tilleggHusnummer",
        "tilleggHusbokstav",
        "tilleggBolignummer",
        "tilleggBydel",
        "tilleggPostboksnr",
        "tilleggPostboksAnlegg",
        "tilleggAdresseFraDato",
        "tilleggAdresseTilDato",
        "tilleggKilde",
        "tilleggRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tillegg {

    @XmlElement(required = true)
    protected String tilleggAdresse1;
    @XmlElement(required = true)
    protected String tilleggAdresse2;
    @XmlElement(required = true)
    protected String tilleggAdresse3;
    @XmlElement(required = true)
    protected String tilleggPostnr;
    @XmlElement(required = true)
    protected String tilleggAdresseDatoTom;
    @XmlElement(required = true)
    protected String tilleggKommunenr;
    @XmlElement(required = true)
    protected String tilleggGateKode;
    @XmlElement(required = true)
    protected String tilleggHusnummer;
    @XmlElement(required = true)
    protected String tilleggHusbokstav;
    @XmlElement(required = true)
    protected String tilleggBolignummer;
    @XmlElement(required = true)
    protected String tilleggBydel;
    @XmlElement(required = true)
    protected String tilleggPostboksnr;
    @XmlElement(required = true)
    protected String tilleggPostboksAnlegg;
    @XmlElement(required = true)
    protected String tilleggAdresseFraDato;
    @XmlElement(required = true)
    protected String tilleggAdresseTilDato;
    @XmlElement(required = true)
    protected String tilleggKilde;
    @XmlElement(required = true)
    protected String tilleggRegistrertAv;

    /**
     * Gets the value of the tilleggAdresse1 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggAdresse1() {
        return tilleggAdresse1;
    }

    /**
     * Sets the value of the tilleggAdresse1 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggAdresse1(String value) {
        this.tilleggAdresse1 = value;
    }

    /**
     * Gets the value of the tilleggAdresse2 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggAdresse2() {
        return tilleggAdresse2;
    }

    /**
     * Sets the value of the tilleggAdresse2 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggAdresse2(String value) {
        this.tilleggAdresse2 = value;
    }

    /**
     * Gets the value of the tilleggAdresse3 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggAdresse3() {
        return tilleggAdresse3;
    }

    /**
     * Sets the value of the tilleggAdresse3 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggAdresse3(String value) {
        this.tilleggAdresse3 = value;
    }

    /**
     * Gets the value of the tilleggPostnr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggPostnr() {
        return tilleggPostnr;
    }

    /**
     * Sets the value of the tilleggPostnr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggPostnr(String value) {
        this.tilleggPostnr = value;
    }

    /**
     * Gets the value of the tilleggAdresseDatoTom property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggAdresseDatoTom() {
        return tilleggAdresseDatoTom;
    }

    /**
     * Sets the value of the tilleggAdresseDatoTom property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggAdresseDatoTom(String value) {
        this.tilleggAdresseDatoTom = value;
    }

    /**
     * Gets the value of the tilleggKommunenr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggKommunenr() {
        return tilleggKommunenr;
    }

    /**
     * Sets the value of the tilleggKommunenr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggKommunenr(String value) {
        this.tilleggKommunenr = value;
    }

    /**
     * Gets the value of the tilleggGateKode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggGateKode() {
        return tilleggGateKode;
    }

    /**
     * Sets the value of the tilleggGateKode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggGateKode(String value) {
        this.tilleggGateKode = value;
    }

    /**
     * Gets the value of the tilleggHusnummer property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggHusnummer() {
        return tilleggHusnummer;
    }

    /**
     * Sets the value of the tilleggHusnummer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggHusnummer(String value) {
        this.tilleggHusnummer = value;
    }

    /**
     * Gets the value of the tilleggHusbokstav property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggHusbokstav() {
        return tilleggHusbokstav;
    }

    /**
     * Sets the value of the tilleggHusbokstav property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggHusbokstav(String value) {
        this.tilleggHusbokstav = value;
    }

    /**
     * Gets the value of the tilleggBolignummer property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggBolignummer() {
        return tilleggBolignummer;
    }

    /**
     * Sets the value of the tilleggBolignummer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggBolignummer(String value) {
        this.tilleggBolignummer = value;
    }

    /**
     * Gets the value of the tilleggBydel property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggBydel() {
        return tilleggBydel;
    }

    /**
     * Sets the value of the tilleggBydel property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggBydel(String value) {
        this.tilleggBydel = value;
    }

    /**
     * Gets the value of the tilleggPostboksnr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggPostboksnr() {
        return tilleggPostboksnr;
    }

    /**
     * Sets the value of the tilleggPostboksnr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggPostboksnr(String value) {
        this.tilleggPostboksnr = value;
    }

    /**
     * Gets the value of the tilleggPostboksAnlegg property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggPostboksAnlegg() {
        return tilleggPostboksAnlegg;
    }

    /**
     * Sets the value of the tilleggPostboksAnlegg property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggPostboksAnlegg(String value) {
        this.tilleggPostboksAnlegg = value;
    }

    /**
     * Gets the value of the tilleggAdresseFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggAdresseFraDato() {
        return tilleggAdresseFraDato;
    }

    /**
     * Sets the value of the tilleggAdresseFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggAdresseFraDato(String value) {
        this.tilleggAdresseFraDato = value;
    }

    /**
     * Gets the value of the tilleggAdresseTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggAdresseTilDato() {
        return tilleggAdresseTilDato;
    }

    /**
     * Sets the value of the tilleggAdresseTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggAdresseTilDato(String value) {
        this.tilleggAdresseTilDato = value;
    }

    /**
     * Gets the value of the tilleggKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggKilde() {
        return tilleggKilde;
    }

    /**
     * Sets the value of the tilleggKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggKilde(String value) {
        this.tilleggKilde = value;
    }

    /**
     * Gets the value of the tilleggRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTilleggRegistrertAv() {
        return tilleggRegistrertAv;
    }

    /**
     * Sets the value of the tilleggRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTilleggRegistrertAv(String value) {
        this.tilleggRegistrertAv = value;
    }

}
