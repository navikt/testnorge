
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for tknrType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tknrType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tknrPerson" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tknrFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tknrTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tknrKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tknregistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tknr", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "tknrPerson",
        "tknrFraDato",
        "tknrTilDato",
        "tknrKilde",
        "tknregistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tknr {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tknrPerson;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tknrFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tknrTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tknrKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tknregistrertAv;

    /**
     * Gets the value of the tknrPerson property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTknrPerson() {
        return tknrPerson;
    }

    /**
     * Sets the value of the tknrPerson property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTknrPerson(String value) {
        this.tknrPerson = value;
    }

    /**
     * Gets the value of the tknrFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTknrFraDato() {
        return tknrFraDato;
    }

    /**
     * Sets the value of the tknrFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTknrFraDato(String value) {
        this.tknrFraDato = value;
    }

    /**
     * Gets the value of the tknrTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTknrTilDato() {
        return tknrTilDato;
    }

    /**
     * Sets the value of the tknrTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTknrTilDato(String value) {
        this.tknrTilDato = value;
    }

    /**
     * Gets the value of the tknrKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTknrKilde() {
        return tknrKilde;
    }

    /**
     * Sets the value of the tknrKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTknrKilde(String value) {
        this.tknrKilde = value;
    }

    /**
     * Gets the value of the tknregistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTknregistrertAv() {
        return tknregistrertAv;
    }

    /**
     * Sets the value of the tknregistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTknregistrertAv(String value) {
        this.tknregistrertAv = value;
    }

}
