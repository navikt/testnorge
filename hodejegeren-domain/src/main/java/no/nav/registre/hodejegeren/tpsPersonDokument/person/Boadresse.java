
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i boadressen
 *
 * <p>Java class for boadresseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="boadresseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="boAdresse" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="boKodeLand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="boKommune" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="boPostnr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="boBydel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="boAdressetillegg" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="booffaGateKode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="booffaHusnr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="booffaBokstav" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="booffaBolignr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bomatrGardsnr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bomatrBruksnr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bomatrFestenr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bomatrUndernr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="boAdresseFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="boAdresseTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="boAdresseKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "boadresse", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "boAdresse",
        "boKodeLand",
        "boKommune",
        "boPostnr",
        "boBydel",
        "boAdressetillegg",
        "booffaGateKode",
        "booffaHusnr",
        "booffaBokstav",
        "booffaBolignr",
        "bomatrGardsnr",
        "bomatrBruksnr",
        "bomatrFestenr",
        "bomatrUndernr",
        "boAdresseFraDato",
        "boAdresseTilDato",
        "boAdresseKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Boadresse {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boAdresse;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boKodeLand;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boKommune;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boPostnr;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boBydel;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boAdressetillegg;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String booffaGateKode;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String booffaHusnr;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String booffaBokstav;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String booffaBolignr;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String bomatrGardsnr;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String bomatrBruksnr;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String bomatrFestenr;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String bomatrUndernr;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boAdresseFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boAdresseTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String boAdresseKilde;

    /**
     * Gets the value of the boAdresse property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoAdresse() {
        return boAdresse;
    }

    /**
     * Sets the value of the boAdresse property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoAdresse(String value) {
        this.boAdresse = value;
    }

    /**
     * Gets the value of the boKodeLand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoKodeLand() {
        return boKodeLand;
    }

    /**
     * Sets the value of the boKodeLand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoKodeLand(String value) {
        this.boKodeLand = value;
    }

    /**
     * Gets the value of the boKommune property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoKommune() {
        return boKommune;
    }

    /**
     * Sets the value of the boKommune property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoKommune(String value) {
        this.boKommune = value;
    }

    /**
     * Gets the value of the boPostnr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoPostnr() {
        return boPostnr;
    }

    /**
     * Sets the value of the boPostnr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoPostnr(String value) {
        this.boPostnr = value;
    }

    /**
     * Gets the value of the boBydel property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoBydel() {
        return boBydel;
    }

    /**
     * Sets the value of the boBydel property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoBydel(String value) {
        this.boBydel = value;
    }

    /**
     * Gets the value of the boAdressetillegg property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoAdressetillegg() {
        return boAdressetillegg;
    }

    /**
     * Sets the value of the boAdressetillegg property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoAdressetillegg(String value) {
        this.boAdressetillegg = value;
    }

    /**
     * Gets the value of the booffaGateKode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBooffaGateKode() {
        return booffaGateKode;
    }

    /**
     * Sets the value of the booffaGateKode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBooffaGateKode(String value) {
        this.booffaGateKode = value;
    }

    /**
     * Gets the value of the booffaHusnr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBooffaHusnr() {
        return booffaHusnr;
    }

    /**
     * Sets the value of the booffaHusnr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBooffaHusnr(String value) {
        this.booffaHusnr = value;
    }

    /**
     * Gets the value of the booffaBokstav property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBooffaBokstav() {
        return booffaBokstav;
    }

    /**
     * Sets the value of the booffaBokstav property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBooffaBokstav(String value) {
        this.booffaBokstav = value;
    }

    /**
     * Gets the value of the booffaBolignr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBooffaBolignr() {
        return booffaBolignr;
    }

    /**
     * Sets the value of the booffaBolignr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBooffaBolignr(String value) {
        this.booffaBolignr = value;
    }

    /**
     * Gets the value of the bomatrGardsnr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBomatrGardsnr() {
        return bomatrGardsnr;
    }

    /**
     * Sets the value of the bomatrGardsnr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBomatrGardsnr(String value) {
        this.bomatrGardsnr = value;
    }

    /**
     * Gets the value of the bomatrBruksnr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBomatrBruksnr() {
        return bomatrBruksnr;
    }

    /**
     * Sets the value of the bomatrBruksnr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBomatrBruksnr(String value) {
        this.bomatrBruksnr = value;
    }

    /**
     * Gets the value of the bomatrFestenr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBomatrFestenr() {
        return bomatrFestenr;
    }

    /**
     * Sets the value of the bomatrFestenr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBomatrFestenr(String value) {
        this.bomatrFestenr = value;
    }

    /**
     * Gets the value of the bomatrUndernr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBomatrUndernr() {
        return bomatrUndernr;
    }

    /**
     * Sets the value of the bomatrUndernr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBomatrUndernr(String value) {
        this.bomatrUndernr = value;
    }

    /**
     * Gets the value of the boAdresseFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoAdresseFraDato() {
        return boAdresseFraDato;
    }

    /**
     * Sets the value of the boAdresseFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoAdresseFraDato(String value) {
        this.boAdresseFraDato = value;
    }

    /**
     * Gets the value of the boAdresseTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoAdresseTilDato() {
        return boAdresseTilDato;
    }

    /**
     * Sets the value of the boAdresseTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoAdresseTilDato(String value) {
        this.boAdresseTilDato = value;
    }

    /**
     * Gets the value of the boAdresseKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBoAdresseKilde() {
        return boAdresseKilde;
    }

    /**
     * Sets the value of the boAdresseKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBoAdresseKilde(String value) {
        this.boAdresseKilde = value;
    }

}
