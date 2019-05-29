
package no.nav.registre.hodejegeren.tpsPersonDokument.person.telefon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for telefonJobbType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="telefonJobbType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tlfJobbRetningslinje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfJobbNummer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfJobbFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfJobbTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfJobbKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfJobbRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "telefonJobb", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "tlfJobbRetningslinje",
        "tlfJobbNummer",
        "tlfJobbFraDato",
        "tlfJobbTilDato",
        "tlfJobbKilde",
        "tlfJobbRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefonJobb {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfJobbRetningslinje;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfJobbNummer;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfJobbFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfJobbTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfJobbKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfJobbRegistrertAv;

    /**
     * Gets the value of the tlfJobbRetningslinje property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfJobbRetningslinje() {
        return tlfJobbRetningslinje;
    }

    /**
     * Sets the value of the tlfJobbRetningslinje property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfJobbRetningslinje(String value) {
        this.tlfJobbRetningslinje = value;
    }

    /**
     * Gets the value of the tlfJobbNummer property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfJobbNummer() {
        return tlfJobbNummer;
    }

    /**
     * Sets the value of the tlfJobbNummer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfJobbNummer(String value) {
        this.tlfJobbNummer = value;
    }

    /**
     * Gets the value of the tlfJobbFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfJobbFraDato() {
        return tlfJobbFraDato;
    }

    /**
     * Sets the value of the tlfJobbFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfJobbFraDato(String value) {
        this.tlfJobbFraDato = value;
    }

    /**
     * Gets the value of the tlfJobbTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfJobbTilDato() {
        return tlfJobbTilDato;
    }

    /**
     * Sets the value of the tlfJobbTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfJobbTilDato(String value) {
        this.tlfJobbTilDato = value;
    }

    /**
     * Gets the value of the tlfJobbKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfJobbKilde() {
        return tlfJobbKilde;
    }

    /**
     * Sets the value of the tlfJobbKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfJobbKilde(String value) {
        this.tlfJobbKilde = value;
    }

    /**
     * Gets the value of the tlfJobbRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfJobbRegistrertAv() {
        return tlfJobbRegistrertAv;
    }

    /**
     * Sets the value of the tlfJobbRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfJobbRegistrertAv(String value) {
        this.tlfJobbRegistrertAv = value;
    }

}
