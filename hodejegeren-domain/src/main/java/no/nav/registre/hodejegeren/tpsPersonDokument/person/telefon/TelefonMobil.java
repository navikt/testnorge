
package no.nav.registre.hodejegeren.tpsPersonDokument.person.telefon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for telefonMobilType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="telefonMobilType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tlfMobilRetningslinje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfMobilNummer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfMobilFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfMobilTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfMobilKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tlfMobilRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "telefonMobil", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "tlfMobilRetningslinje",
        "tlfMobilNummer",
        "tlfMobilFraDato",
        "tlfMobilTilDato",
        "tlfMobilKilde",
        "tlfMobilRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefonMobil {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfMobilRetningslinje;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfMobilNummer;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfMobilFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfMobilTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfMobilKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tlfMobilRegistrertAv;

    /**
     * Gets the value of the tlfMobilRetningslinje property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfMobilRetningslinje() {
        return tlfMobilRetningslinje;
    }

    /**
     * Sets the value of the tlfMobilRetningslinje property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfMobilRetningslinje(String value) {
        this.tlfMobilRetningslinje = value;
    }

    /**
     * Gets the value of the tlfMobilNummer property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfMobilNummer() {
        return tlfMobilNummer;
    }

    /**
     * Sets the value of the tlfMobilNummer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfMobilNummer(String value) {
        this.tlfMobilNummer = value;
    }

    /**
     * Gets the value of the tlfMobilFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfMobilFraDato() {
        return tlfMobilFraDato;
    }

    /**
     * Sets the value of the tlfMobilFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfMobilFraDato(String value) {
        this.tlfMobilFraDato = value;
    }

    /**
     * Gets the value of the tlfMobilTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfMobilTilDato() {
        return tlfMobilTilDato;
    }

    /**
     * Sets the value of the tlfMobilTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfMobilTilDato(String value) {
        this.tlfMobilTilDato = value;
    }

    /**
     * Gets the value of the tlfMobilKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfMobilKilde() {
        return tlfMobilKilde;
    }

    /**
     * Sets the value of the tlfMobilKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfMobilKilde(String value) {
        this.tlfMobilKilde = value;
    }

    /**
     * Gets the value of the tlfMobilRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTlfMobilRegistrertAv() {
        return tlfMobilRegistrertAv;
    }

    /**
     * Sets the value of the tlfMobilRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTlfMobilRegistrertAv(String value) {
        this.tlfMobilRegistrertAv = value;
    }

}
