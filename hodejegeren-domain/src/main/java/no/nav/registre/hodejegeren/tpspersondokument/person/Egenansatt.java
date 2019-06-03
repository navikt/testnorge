
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene for egenansatt.
 *
 * <p>Java class for egenansattType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="egenansattType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="egenansatt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="egenansattFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="egenansattTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="egenansattKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="egenansattRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "egenansatt", propOrder = {
        "egenansatt",
        "egenansattFraDato",
        "egenansattTilDato",
        "egenansattKilde",
        "egenansattRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Egenansatt {

    @XmlElement(required = true)
    protected String egenansatt;
    @XmlElement(required = true)
    protected String egenansattFraDato;
    @XmlElement(required = true)
    protected String egenansattTilDato;
    @XmlElement(required = true)
    protected String egenansattKilde;
    @XmlElement(required = true)
    protected String egenansattRegistrertAv;

    /**
     * Gets the value of the egenansatt property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEgenansatt() {
        return egenansatt;
    }

    /**
     * Sets the value of the egenansatt property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEgenansatt(String value) {
        this.egenansatt = value;
    }

    /**
     * Gets the value of the egenansattFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEgenansattFraDato() {
        return egenansattFraDato;
    }

    /**
     * Sets the value of the egenansattFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEgenansattFraDato(String value) {
        this.egenansattFraDato = value;
    }

    /**
     * Gets the value of the egenansattTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEgenansattTilDato() {
        return egenansattTilDato;
    }

    /**
     * Sets the value of the egenansattTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEgenansattTilDato(String value) {
        this.egenansattTilDato = value;
    }

    /**
     * Gets the value of the egenansattKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEgenansattKilde() {
        return egenansattKilde;
    }

    /**
     * Sets the value of the egenansattKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEgenansattKilde(String value) {
        this.egenansattKilde = value;
    }

    /**
     * Gets the value of the egenansattRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEgenansattRegistrertAv() {
        return egenansattRegistrertAv;
    }

    /**
     * Sets the value of the egenansattRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEgenansattRegistrertAv(String value) {
        this.egenansattRegistrertAv = value;
    }

}
