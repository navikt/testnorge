
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i et enkelt vergemaal.
 *
 * <p>Java class for vergeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="vergeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vergeSaksid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeSakstype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeMandattype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeEmbete" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeVedtakDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeFnr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vergeKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verge", propOrder = {
        "vergeSaksid",
        "vergeId",
        "vergeType",
        "vergeSakstype",
        "vergeMandattype",
        "vergeEmbete",
        "vergeVedtakDato",
        "vergeFnr",
        "vergeFraDato",
        "vergeTilDato",
        "vergeKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verge {

    @XmlElement(required = true)
    protected String vergeSaksid;
    @XmlElement(required = true)
    protected String vergeId;
    @XmlElement(required = true)
    protected String vergeType;
    @XmlElement(required = true)
    protected String vergeSakstype;
    @XmlElement(required = true)
    protected String vergeMandattype;
    @XmlElement(required = true)
    protected String vergeEmbete;
    @XmlElement(required = true)
    protected String vergeVedtakDato;
    @XmlElement(required = true)
    protected String vergeFnr;
    @XmlElement(required = true)
    protected String vergeFraDato;
    @XmlElement(required = true)
    protected String vergeTilDato;
    @XmlElement(required = true)
    protected String vergeKilde;

    /**
     * Gets the value of the vergeSaksid property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeSaksid() {
        return vergeSaksid;
    }

    /**
     * Sets the value of the vergeSaksid property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeSaksid(String value) {
        this.vergeSaksid = value;
    }

    /**
     * Gets the value of the vergeId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeId() {
        return vergeId;
    }

    /**
     * Sets the value of the vergeId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeId(String value) {
        this.vergeId = value;
    }

    /**
     * Gets the value of the vergeType property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeType() {
        return vergeType;
    }

    /**
     * Sets the value of the vergeType property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeType(String value) {
        this.vergeType = value;
    }

    /**
     * Gets the value of the vergeSakstype property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeSakstype() {
        return vergeSakstype;
    }

    /**
     * Sets the value of the vergeSakstype property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeSakstype(String value) {
        this.vergeSakstype = value;
    }

    /**
     * Gets the value of the vergeMandattype property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeMandattype() {
        return vergeMandattype;
    }

    /**
     * Sets the value of the vergeMandattype property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeMandattype(String value) {
        this.vergeMandattype = value;
    }

    /**
     * Gets the value of the vergeEmbete property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeEmbete() {
        return vergeEmbete;
    }

    /**
     * Sets the value of the vergeEmbete property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeEmbete(String value) {
        this.vergeEmbete = value;
    }

    /**
     * Gets the value of the vergeVedtakDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeVedtakDato() {
        return vergeVedtakDato;
    }

    /**
     * Sets the value of the vergeVedtakDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeVedtakDato(String value) {
        this.vergeVedtakDato = value;
    }

    /**
     * Gets the value of the vergeFnr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeFnr() {
        return vergeFnr;
    }

    /**
     * Sets the value of the vergeFnr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeFnr(String value) {
        this.vergeFnr = value;
    }

    /**
     * Gets the value of the vergeFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeFraDato() {
        return vergeFraDato;
    }

    /**
     * Sets the value of the vergeFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeFraDato(String value) {
        this.vergeFraDato = value;
    }

    /**
     * Gets the value of the vergeTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeTilDato() {
        return vergeTilDato;
    }

    /**
     * Sets the value of the vergeTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeTilDato(String value) {
        this.vergeTilDato = value;
    }

    /**
     * Gets the value of the vergeKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVergeKilde() {
        return vergeKilde;
    }

    /**
     * Sets the value of the vergeKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVergeKilde(String value) {
        this.vergeKilde = value;
    }

}
