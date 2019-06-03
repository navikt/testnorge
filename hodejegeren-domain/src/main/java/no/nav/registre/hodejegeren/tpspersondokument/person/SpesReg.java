
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for spesRegType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="spesRegType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="spesReg" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spesRegFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spesRegTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spesRegKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spesRegRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spesReg", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "spesReg",
        "spesRegFraDato",
        "spesRegTilDato",
        "spesRegKilde",
        "spesRegRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpesReg {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String spesReg;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String spesRegFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String spesRegTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String spesRegKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String spesRegRegistrertAv;

    /**
     * Gets the value of the spesReg property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpesReg() {
        return spesReg;
    }

    /**
     * Sets the value of the spesReg property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpesReg(String value) {
        this.spesReg = value;
    }

    /**
     * Gets the value of the spesRegFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpesRegFraDato() {
        return spesRegFraDato;
    }

    /**
     * Sets the value of the spesRegFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpesRegFraDato(String value) {
        this.spesRegFraDato = value;
    }

    /**
     * Gets the value of the spesRegTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpesRegTilDato() {
        return spesRegTilDato;
    }

    /**
     * Sets the value of the spesRegTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpesRegTilDato(String value) {
        this.spesRegTilDato = value;
    }

    /**
     * Gets the value of the spesRegKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpesRegKilde() {
        return spesRegKilde;
    }

    /**
     * Sets the value of the spesRegKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpesRegKilde(String value) {
        this.spesRegKilde = value;
    }

    /**
     * Gets the value of the spesRegRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpesRegRegistrertAv() {
        return spesRegRegistrertAv;
    }

    /**
     * Sets the value of the spesRegRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpesRegRegistrertAv(String value) {
        this.spesRegRegistrertAv = value;
    }

}
