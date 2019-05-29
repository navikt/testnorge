
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene vedr dod.
 *
 * <p>Java class for dodType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="dodType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datoDod" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dodDatoReg" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dodKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dodRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dod", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "datoDod",
        "dodDatoReg",
        "dodKilde",
        "dodRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dod {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String datoDod;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dodDatoReg;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dodKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String dodRegistrertAv;

    /**
     * Gets the value of the datoDod property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDatoDod() {
        return datoDod;
    }

    /**
     * Sets the value of the datoDod property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDatoDod(String value) {
        this.datoDod = value;
    }

    /**
     * Gets the value of the dodDatoReg property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDodDatoReg() {
        return dodDatoReg;
    }

    /**
     * Sets the value of the dodDatoReg property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDodDatoReg(String value) {
        this.dodDatoReg = value;
    }

    /**
     * Gets the value of the dodKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDodKilde() {
        return dodKilde;
    }

    /**
     * Sets the value of the dodKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDodKilde(String value) {
        this.dodKilde = value;
    }

    /**
     * Gets the value of the dodRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDodRegistrertAv() {
        return dodRegistrertAv;
    }

    /**
     * Sets the value of the dodRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDodRegistrertAv(String value) {
        this.dodRegistrertAv = value;
    }

}
