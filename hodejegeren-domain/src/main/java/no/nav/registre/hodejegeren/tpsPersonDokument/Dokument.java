
package no.nav.registre.hodejegeren.tpsPersonDokument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver metdata om persondokumentet
 *
 * <p>Java class for dokumentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="dokumentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dokumentstatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dokumentutfyllendemelding" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dokumenttidspunkt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dokumenthistorikk" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dokumentFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dokumentTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dokumentKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dokument", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "dokumentstatus",
        "dokumentutfyllendemelding",
        "dokumenttidspunkt",
        "dokumenthistorikk",
        "dokumentFraDato",
        "dokumentTilDato",
        "dokumentKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dokument {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dokumentstatus;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dokumentutfyllendemelding;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dokumenttidspunkt;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dokumenthistorikk;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dokumentFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dokumentTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dokumentKilde;

    /**
     * Gets the value of the dokumentstatus property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDokumentstatus() {
        return dokumentstatus;
    }

    /**
     * Sets the value of the dokumentstatus property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDokumentstatus(String value) {
        this.dokumentstatus = value;
    }

    /**
     * Gets the value of the dokumentutfyllendemelding property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDokumentutfyllendemelding() {
        return dokumentutfyllendemelding;
    }

    /**
     * Sets the value of the dokumentutfyllendemelding property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDokumentutfyllendemelding(String value) {
        this.dokumentutfyllendemelding = value;
    }

    /**
     * Gets the value of the dokumenttidspunkt property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDokumenttidspunkt() {
        return dokumenttidspunkt;
    }

    /**
     * Sets the value of the dokumenttidspunkt property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDokumenttidspunkt(String value) {
        this.dokumenttidspunkt = value;
    }

    /**
     * Gets the value of the dokumenthistorikk property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDokumenthistorikk() {
        return dokumenthistorikk;
    }

    /**
     * Sets the value of the dokumenthistorikk property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDokumenthistorikk(String value) {
        this.dokumenthistorikk = value;
    }

    /**
     * Gets the value of the dokumentFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDokumentFraDato() {
        return dokumentFraDato;
    }

    /**
     * Sets the value of the dokumentFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDokumentFraDato(String value) {
        this.dokumentFraDato = value;
    }

    /**
     * Gets the value of the dokumentTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDokumentTilDato() {
        return dokumentTilDato;
    }

    /**
     * Sets the value of the dokumentTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDokumentTilDato(String value) {
        this.dokumentTilDato = value;
    }

    /**
     * Gets the value of the dokumentKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDokumentKilde() {
        return dokumentKilde;
    }

    /**
     * Sets the value of the dokumentKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDokumentKilde(String value) {
        this.dokumentKilde = value;
    }

}
