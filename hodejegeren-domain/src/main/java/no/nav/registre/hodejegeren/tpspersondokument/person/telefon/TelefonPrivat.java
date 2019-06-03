
package no.nav.registre.hodejegeren.tpspersondokument.person.telefon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for telefonPrivatType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="telefonPrivatType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tlfPrivatRetningslinje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfPrivatNummer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfPrivatFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfPrivatTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfPrivatKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfPrivatRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "telefonPrivat", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "tlfPrivatRetningslinje",
        "tlfPrivatNummer",
        "tlfPrivatFraDato",
        "tlfPrivatTilDato",
        "tlfPrivatKilde",
        "tlfPrivatRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefonPrivat {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfPrivatRetningslinje;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfPrivatNummer;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfPrivatFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfPrivatTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfPrivatKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfPrivatRegistrertAv;

    /**
     * Gets the value of the tlfPrivatRetningslinje property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfPrivatRetningslinje() {
        return tlfPrivatRetningslinje;
    }

    /**
     * Sets the value of the tlfPrivatRetningslinje property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfPrivatRetningslinje(String value) {
        this.tlfPrivatRetningslinje = value;
    }

    /**
     * Gets the value of the tlfPrivatNummer property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfPrivatNummer() {
        return tlfPrivatNummer;
    }

    /**
     * Sets the value of the tlfPrivatNummer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfPrivatNummer(String value) {
        this.tlfPrivatNummer = value;
    }

    /**
     * Gets the value of the tlfPrivatFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfPrivatFraDato() {
        return tlfPrivatFraDato;
    }

    /**
     * Sets the value of the tlfPrivatFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfPrivatFraDato(String value) {
        this.tlfPrivatFraDato = value;
    }

    /**
     * Gets the value of the tlfPrivatTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfPrivatTilDato() {
        return tlfPrivatTilDato;
    }

    /**
     * Sets the value of the tlfPrivatTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfPrivatTilDato(String value) {
        this.tlfPrivatTilDato = value;
    }

    /**
     * Gets the value of the tlfPrivatKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfPrivatKilde() {
        return tlfPrivatKilde;
    }

    /**
     * Sets the value of the tlfPrivatKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfPrivatKilde(String value) {
        this.tlfPrivatKilde = value;
    }

    /**
     * Gets the value of the tlfPrivatRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfPrivatRegistrertAv() {
        return tlfPrivatRegistrertAv;
    }

    /**
     * Sets the value of the tlfPrivatRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfPrivatRegistrertAv(String value) {
        this.tlfPrivatRegistrertAv = value;
    }

}
