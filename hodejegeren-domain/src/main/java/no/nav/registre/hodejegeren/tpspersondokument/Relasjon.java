
package no.nav.registre.hodejegeren.tpspersondokument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i en relasjon.
 *
 * <p>Java class for relasjonType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="relasjonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="relasjonIdent" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relasjonIdentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relasjonIdentStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relasjonRolle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relasjonFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relasjonTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relasjonKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relasjonRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relasjon", propOrder = {
        "relasjonIdent",
        "relasjonIdentType",
        "relasjonIdentStatus",
        "relasjonRolle",
        "relasjonFraDato",
        "relasjonTilDato",
        "relasjonKilde",
        "relasjonRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relasjon {

    @XmlElement(required = true)
    protected String relasjonIdent;
    @XmlElement(required = true)
    protected String relasjonIdentType;
    @XmlElement(required = true)
    protected String relasjonIdentStatus;
    @XmlElement(required = true)
    protected String relasjonRolle;
    @XmlElement(required = true)
    protected String relasjonFraDato;
    @XmlElement(required = true)
    protected String relasjonTilDato;
    @XmlElement(required = true)
    protected String relasjonKilde;
    @XmlElement(required = true)
    protected String relasjonRegistrertAv;

    /**
     * Gets the value of the relasjonIdent property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelasjonIdent() {
        return relasjonIdent;
    }

    /**
     * Sets the value of the relasjonIdent property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelasjonIdent(String value) {
        this.relasjonIdent = value;
    }

    /**
     * Gets the value of the relasjonIdentType property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelasjonIdentType() {
        return relasjonIdentType;
    }

    /**
     * Sets the value of the relasjonIdentType property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelasjonIdentType(String value) {
        this.relasjonIdentType = value;
    }

    /**
     * Gets the value of the relasjonIdentStatus property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelasjonIdentStatus() {
        return relasjonIdentStatus;
    }

    /**
     * Sets the value of the relasjonIdentStatus property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelasjonIdentStatus(String value) {
        this.relasjonIdentStatus = value;
    }

    /**
     * Gets the value of the relasjonRolle property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelasjonRolle() {
        return relasjonRolle;
    }

    /**
     * Sets the value of the relasjonRolle property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelasjonRolle(String value) {
        this.relasjonRolle = value;
    }

    /**
     * Gets the value of the relasjonFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelasjonFraDato() {
        return relasjonFraDato;
    }

    /**
     * Sets the value of the relasjonFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelasjonFraDato(String value) {
        this.relasjonFraDato = value;
    }

    /**
     * Gets the value of the relasjonTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelasjonTilDato() {
        return relasjonTilDato;
    }

    /**
     * Sets the value of the relasjonTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelasjonTilDato(String value) {
        this.relasjonTilDato = value;
    }

    /**
     * Gets the value of the relasjonKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelasjonKilde() {
        return relasjonKilde;
    }

    /**
     * Sets the value of the relasjonKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelasjonKilde(String value) {
        this.relasjonKilde = value;
    }

    /**
     * Gets the value of the relasjonRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelasjonRegistrertAv() {
        return relasjonRegistrertAv;
    }

    /**
     * Sets the value of the relasjonRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelasjonRegistrertAv(String value) {
        this.relasjonRegistrertAv = value;
    }

}
