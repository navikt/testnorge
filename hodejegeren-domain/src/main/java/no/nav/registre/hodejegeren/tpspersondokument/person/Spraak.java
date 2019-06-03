
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene for spraak.
 *
 * <p>Java class for spraakType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="spraakType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="spraak" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spraakFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spraakTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spraakKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spraakRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spraak", propOrder = {
        "spraak",
        "spraakFraDato",
        "spraakTilDato",
        "spraakKilde",
        "spraakRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spraak {

    @XmlElement(required = true)
    protected String spraak;
    @XmlElement(required = true)
    protected String spraakFraDato;
    @XmlElement(required = true)
    protected String spraakTilDato;
    @XmlElement(required = true)
    protected String spraakKilde;
    @XmlElement(required = true)
    protected String spraakRegistrertAv;

    /**
     * Gets the value of the spraak property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpraak() {
        return spraak;
    }

    /**
     * Sets the value of the spraak property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpraak(String value) {
        this.spraak = value;
    }

    /**
     * Gets the value of the spraakFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpraakFraDato() {
        return spraakFraDato;
    }

    /**
     * Sets the value of the spraakFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpraakFraDato(String value) {
        this.spraakFraDato = value;
    }

    /**
     * Gets the value of the spraakTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpraakTilDato() {
        return spraakTilDato;
    }

    /**
     * Sets the value of the spraakTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpraakTilDato(String value) {
        this.spraakTilDato = value;
    }

    /**
     * Gets the value of the spraakKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpraakKilde() {
        return spraakKilde;
    }

    /**
     * Sets the value of the spraakKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpraakKilde(String value) {
        this.spraakKilde = value;
    }

    /**
     * Gets the value of the spraakRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpraakRegistrertAv() {
        return spraakRegistrertAv;
    }

    /**
     * Sets the value of the spraakRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpraakRegistrertAv(String value) {
        this.spraakRegistrertAv = value;
    }

}
